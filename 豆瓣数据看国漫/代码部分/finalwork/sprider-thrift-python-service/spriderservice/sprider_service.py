# 爬虫程序所需要的类包
import random
import time

import requests
import selenium
from selenium import webdriver
from bs4 import BeautifulSoup
import json

import pymongo

# 启动服务的类包
from thrift.server import TServer
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
# 引入thrift生成的接口文件
from spriderservice.api import SpriderService

client = pymongo.MongoClient('192.168.0.109')
db = client['spriderDB']
MONGO_TABLE = 'douban_guoman'

class sprider:
    def spr_main(self):
        print("开始爬取数据")

        proxy_url = 'https://proxyapi.horocn.com/api/v2/proxies?order_id=KSTJ1673185384008565&num=10' \
                    '&format=text&line_separator=win&can_repeat=yes&user_token=12e8b4c7a5c8349ff125d781d7713a63'

        resp = requests.get(proxy_url)

        ips = resp.text.split('\r\n')

        # 获取ID信息, 作为爬取依据
        with open("IDs.json", "r")as jsiD:
            ids = json.load(jsiD)

        # # 初始启动爬虫, 创建infos
        # infos = []

        # 中途重新启动爬虫, 读取先前数据
        with open("info.json", "r", encoding='UTF-8')as info_file:
            infos = json.load(info_file)

        count = 0
        j = 0

        options = webdriver.ChromeOptions()
        options.add_argument('--disable-gpu')
        prefs = {
            'profile.default_content_setting_values': {
                # 'images': 2,  # 屏蔽图片
                # 'javascript': 2,  # 屏蔽js
            }
        }


        options.add_experimental_option("prefs", prefs)
        # options.add_argument('--headless')
        # options.add_argument("--proxy-server=http://" + ips[j])
        options.add_argument('–no-sandbox')
        driver = webdriver.Chrome(chrome_options=options, executable_path='e:/chromedriver_win32/chromedriver.exe')
        driver.set_page_load_timeout(10)
        driver.set_script_timeout(10)


        while count < len(id):
            # 目标url, 通过IDs.json获取 id 进行访问
            url = "https://movie.douban.com/subject/{}/".format(ids[count])

            # 测试是否可访问
            try:
                driver.get(url)
                # 字典, 用于存放单个影片数据
                info = {}

                # 获取标题信息
                info["标题"] = driver.find_element_by_css_selector(
                    '#content > h1 > span[property="v:itemreviewed"]').get_attribute('textContent')
            except Exception:
                # 不能访问, 输出"失败", 手动重启
                driver.close()
                driver.quit()
                print(count, "失败")
                time.sleep(5)
                continue

            # 获取年份信息
            try:
                info["年份"] = driver.find_element_by_css_selector('#content > h1 > span.year').get_attribute('textContent')
            except:
                info["年份"] = '无'

            # 获取评分信息, 若无评分默认为"无"
            try:
                info["评分"] = driver.find_element_by_css_selector(
                    '#interest_sectl > div > div.rating_self.clearfix > strong').text
                info["评分人数"] = driver.find_element_by_css_selector('#interest_sectl > div > div.rating_self.clearfix > '
                                                                   'div > div.rating_sum > a > span').get_attribute(
                    'textContent')
                info["5星比例"] = driver.find_element_by_css_selector('#interest_sectl > div > div.ratings-on-weight > '
                                                                   'div:nth-child(1) > span.rating_per').get_attribute(
                    'textContent')
                info["4星比例"] = driver.find_element_by_css_selector('#interest_sectl > div > div.ratings-on-weight > '
                                                                   'div:nth-child(2) > span.rating_per').get_attribute(
                    'textContent')
                info["3星比例"] = driver.find_element_by_css_selector('#interest_sectl > div > div.ratings-on-weight > '
                                                                   'div:nth-child(3) > span.rating_per').get_attribute(
                    'textContent')
                info["2星比例"] = driver.find_element_by_css_selector('#interest_sectl > div > div.ratings-on-weight > '
                                                                   'div:nth-child(4) > span.rating_per').get_attribute(
                    'textContent')
                info["1星比例"] = driver.find_element_by_css_selector('#interest_sectl > div > div.ratings-on-weight > '
                                                                   'div:nth-child(5) > span.rating_per').get_attribute(
                    'textContent')
            except Exception:
                info["评分"] = "暂无评分"
                info["评分人数"] = "0"
                info["5星比例"] = "0"
                info["4星比例"] = "0"
                info["3星比例"] = "0"
                info["2星比例"] = "0"
                info["1星比例"] = "0"

            # 获取导演信息
            try:
                info["导演"] = driver.find_element_by_css_selector("#info > span:nth-child(1) > span.attrs "
                                                                 "> a").get_attribute('textContent').split(' / ')
            except Exception:
                info["导演"] = []

            # 获取编剧信息
            try:
                info["编剧"] = driver.find_element_by_css_selector('#info > span:nth-child(3) > '
                                                                 'span.attrs').get_attribute('textContent').split(' / ')
            except Exception:
                info["编剧"] = []

            # 获取演员信息
            try:
                actors = []
                actor_lists = driver.find_elements_by_css_selector(' a[rel="v:starring"]')
                for actor in actor_lists:
                    actors.append(actor.get_attribute('textContent'))
                info["主演"] = actors
            except Exception:
                info["主演"] = []

            # 将div.info转换为字符串并进行切分来获取其他相关信息
            div = driver.find_element_by_css_selector('#info').text
            div_list = div.split("\n")
            for di_li in div_list:
                if di_li[0: 2] == '制片':
                    info["制片国家/地区"] = di_li[9:].split('/')
                if di_li[0:2] == '集数':
                    jishu = di_li[4:]
                if di_li[0:4] == '单集片长':
                    danji = di_li[6:]
                if di_li[0:2] == '片长':
                    info["片长"] = di_li[4:]

            # 若同时存在集数和单集片长则判定为不是电影, 否则为电影
            try:
                jishu
                danji
            except NameError:
                info["是电影"] = 1
            else:
                info["片长"] = [jishu, danji]
                del jishu
                del danji
                info["是电影"] = 0

            # 存在部分片长或单集片长均不存在, 将其设为"无"
            try:
                info["片长"]
            except KeyError:
                info["片长"] = "无"

            # 尝试获取豆瓣成员标签
            try:
                tags = []
                tag = driver.find_elements_by_css_selector('#content > div.grid-16-8.clearfix > div.aside > '
                                                           'div.tags > div > a')
                for t in tag:
                    t_text = t.get_attribute('textContent')
                    tags.append(t_text)
                info["豆瓣成员标签"] = tags
            except:
                info["豆瓣成员标签"] = "无"

            # 获取看过人数信息
            try:
                info["看过"] = driver.find_element_by_css_selector('#subject-others-interests > div > '
                                                                 'a[href="https://movie.douban.com/subject/{}/collections"]'.
                                                                 format(ids[count])).get_attribute('textContent')
            except:
                info["看过"] = '0'

            # 获取类型信息
            try:
                tps = []
                types = driver.find_elements_by_xpath("//div[@id='info']/span[@property='v:genre']")
                for type in types:
                    tps.append(type.get_attribute('textContent'))
                info["类型"] = tps
            except Exception:
                print(Exception)

            # 将单个影片信息存入总信息中
            infos.append(info)

            # 休眠随机时长
            r = random.randint(2, 4)
            time.sleep(r)
            # driver.close()
            # driver.quit()

            # 每次 infos 加入新信息后进行覆写保存, 方便中断后继续爬取
            # with open("info.json", "w", encoding='UTF-8')as in_file:
            #     in_file.write(json.dumps(infos, indent=4, ensure_ascii=False))
            db[MONGO_TABLE].remove()
            db[MONGO_TABLE].insert_one(info)
            # 输出当前进度
            print(count)
            # 进行下一次爬取
            count = count + 1


if __name__ == '__main__':
    # 创建主方法
    handler = sprider()
    processor =SpriderService.Processor(handler)

    # 1. 创建 Thrift Server 的 ServerSocket

    serverSocket = TSocket.TServerSocket(host='127.0.0.1', port='9090')

    # 2. 创建 Thrift Server 的 Transport = 帧传输方式
    transportFactory = TTransport.TFramedTransportFactory()

    # 3. 创建 Thrift Server 的 Protocol  = 二进制传输协议
    protocolFactory = TBinaryProtocol.TBinaryProtocolFactory()

    # 5. 创建 Thrift Server, 指明如何处理(谁进行处理)、从哪来(监听哪个端口)、怎么传来(传输方式)、传输协议
    thriftServer = TServer.TSimpleServer(processor, serverSocket, transportFactory, protocolFactory)

    # 6. 启动 Thrfit Server, 等待客户端的访问

    print("Python Thrift Server start....")
    thriftServer.serve()
    print("Python Thrift Server exit")