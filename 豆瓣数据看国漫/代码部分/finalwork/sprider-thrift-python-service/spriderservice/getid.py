import requests
from selenium import webdriver
from bs4 import BeautifulSoup
import json
import time
import random

# 设置请求头
requests_headers = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) "
                  "Chrome/83.0.4103.116 Safari/537.36 Edg/83.0.478.56 "
}
# 设置参数


print("开始获取代理IP")

proxy_url = 'https://proxyapi.horocn.com/api/v2/proxies?order_id=14XZ1673181751463235&num=' \
            '20&format=text&line_separator=win&can_repeat=yes&user_token=12e8b4c7a5c8349ff125d781d7713a63'

resp = requests.get(proxy_url)

ips = resp.text.split('\r\n')
# with open("ip.json", "r")as jsip:
#     ips = json.load(jsip)


# ids = []

with open("IDs.json", "r")as jsiD:
    ids = json.load(jsiD)

j = 0

count = len(ids)
while count <= 4960:
    options = webdriver.ChromeOptions()
    options.add_argument('--headless')
    options.add_argument("--proxy-server=http://" + ips[j])
    driver = webdriver.Chrome(chrome_options=options, executable_path='e:/chromedriver_win32/chromedriver.exe')
    driver.set_page_load_timeout(5)
    driver.set_script_timeout(5)
    # ID信息来源地址
    url = 'https://movie.douban.com/j/new_search_subjects?sort=T&range=0,20&tags=' \
          '%E5%8A%A8%E7%94%BB,%E4%B8%AD%E5%9B%BD%E5%A4%A7%E9%99%86&start={}'.format(count)

    try:
        # 获取页面
        driver.get(url)
        html = driver.page_source
        soup = BeautifulSoup(html, 'html.parser')
        cc = soup.select('pre')[0]
        res = json.loads(cc.text)
        datas = res['data']
    except Exception:
        j = j + 1
        if j == 10:
            resp = requests.get(proxy_url)
            ips = resp.text.split('\r\n')
            j = 0
        print(j)
        time.sleep(5)
        driver.close()
        driver.quit()
        continue

    for data in datas:
        ids.append(data['id'])

    print(count)
    count = count + 20
    r = random.randint(2, 4)
    time.sleep(r)
    driver.close()
    driver.quit()

    with open("IDs.json", "w") as js:
        js.write(json.dumps(ids, indent=4, ensure_ascii=False))
