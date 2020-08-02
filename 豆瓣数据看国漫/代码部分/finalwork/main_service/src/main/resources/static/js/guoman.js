//1-1 折线图 产出数量
(function() {
    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.querySelector(".produce-num"));


    // (1)准备数据
    $.ajax({
        url:"/main/now",
        type: "POST",
        success:function(data){
            var year = data.map(function(item) {
                return item["year"];
            })
            var all = data.map(function(item) {
                return item["allNum"];
            })
            var t1 = data.map(function(item) {
                return item["t1Num"];
            })
            var t0 = data.map(function(item) {
                return item["t0Num"];
            })
            // 2. 指定配置和数据
            var option = {
                color: ["#00f2f1", "#ed3f35", "#f9F900"],
                title: {
                    text: '每年国漫产出数量',
                    left: 'center',
                    top: 0,
                    textStyle: {
                        color: '#ffffff',
                        fontSize:14
                    }

                },
                tooltip: {
                    // 通过坐标轴来触发
                    trigger: "axis"
                },
                legend: {
                    top:20,
                    left:"center",
                    // 修饰图例文字的颜色
                    textStyle: {
                        color: "#4c9bfd",
                        fonSize:12
                    }
                },
                grid: {
                    top: "20%",
                    left: "3%",
                    right: "4%",
                    bottom: "3%",
                    show: true,
                    borderColor: "#012f4a",
                    containLabel: true
                },

                xAxis: {
                    type: "category",
                    boundaryGap: false,
                    data: year,
                    // 去除刻度
                    axisTick: {
                        show: false
                    },
                    // 修饰刻度标签的颜色
                    axisLabel: {
                        color: "rgba(255,255,255,.7)"
                    },
                    // 去除x坐标轴的颜色
                    axisLine: {

                        show: false
                    }
                },
                yAxis: {
                    type: "value",
                    // 去除刻度
                    axisTick: {
                        show: false
                    },
                    // // 修饰刻度标签的颜色
                     axisLabel: {
                       color: "rgba(255,255,255,.7)"
                     },
                    // 修改y轴分割线的颜色
                    splitLine: {
                        lineStyle: {
                            color: "#ffffff"
                        }
                    }
                },
                series: [{
                    name: "总量",
                    type: "line",
                    // stack: "总量",
                    // 是否让线条圆滑显示
                    smooth: true,
                    data: all
                },
                    {
                        name: "电影/短片",
                        type: "line",
                        // stack: "总量",
                        smooth: true,
                        data: t1
                    },
                    {
                        name: "剧集",
                        type: "line",
                        // stack: "总量",
                        smooth: true,
                        data: t0
                    }
                ]
            };
            // 3. 把配置和数据给实例对象
            myChart.setOption(option);

            // 重新把配置好的新数据给实例对象
            myChart.setOption(option);
            window.addEventListener("resize", function() {
                myChart.resize();
            });
        },
        error:function(XMLHttpRequest, textStatus,
                       errorThrown){
            alert("请求失败");
        }
    })
})();
//1-1.2 条形图 产出数量
(function() {
    // 实例化对象
    var myChart = echarts.init(document.querySelector(".produce-len"));
    // 指定配置和数据
    $.ajax({
        url:"/main/test1",
        type: "POST",
        success:function(data) {
            //删除2020年后的

            var year = data.map(function (item) {
                return item["year"];
            })
            var len1 = data.map(function (item) {
                return item["len1"];
            })
            var len0 = data.map(function (item) {
                return item["len0"];
            })
            var option = {
                title: {
                    text: '每年国漫产出时长',
                    left: 'center',
                    top: 0,
                    textStyle: {
                        color: '#ffffff',
                        fontSize:14
                    }

                },
                grid: {
                    left: "0%",
                    top: "10px",
                    right: "0%",
                    bottom: "4%",
                    container: true
                },
                tooltip: {
                    trigger: 'axis'
                },
                legend: {
                    data: ['电影/短片', '剧集'],
                    left: 10,
                    top: 20,
                    textStyle: {
                        color: "rgba(255, 255, 255, 1)",
                        fontSize:12
                    }
                },

                calculable: true,
                xAxis: [{
                    type: 'category',
                    data: year,
                    axisTick: {
                        show: false
                    },
                    // 修饰刻度标签的颜色
                    axisLabel: {
                        color: "rgba(255,255,255,.7)"
                     },
                    // 去除x坐标轴的颜色
                    axisLine: {

                        show: false
                    }
                }],
                yAxis: [{
                    type: 'value',
                    axisTick: {
                        show: false
                    },
                    max: 40000
                }],
                series: [{
                    name: '电影/短片',
                    type: 'bar',
                    data: len1
                },
                    {
                        name: '剧集',
                        type: 'bar',
                        data: len0
                    }
                ]
            };

            // 把配置给实例对象
            myChart.setOption(option);
            window.addEventListener("resize", function() {
                myChart.resize();
            });

            // 数据变化
            var dataAll = [
                { year: "2019", data: [200, 300, 300, 900, 1500, 1200, 600] },
                { year: "2020", data: [300, 400, 350, 800, 1800, 1400, 700] }
            ];

            $(".bar h2 ").on("click", "a", function() {
                option.series[0].data = dataAll[$(this).index()].data;
                myChart.setOption(option);
            });
        }})

})();
//1-2 堆积图 评分
(function() {
    // 实例化对象
    var myChart = echarts.init(document.querySelector(".socre-rank"));
    // 指定配置和数据
    $.ajax({
        url:"/main/now",
        type: "POST",
        success:function(data) {
            var year = data.slice(40,83).map(function (item) {
                return item["year"]
            })
            var r1= data.slice(40,83).map(function (item) {
                return item["rank1"]/item["allNum"]*100;
            })
            var r2 = data.slice(40,83).map(function (item) {
                return item["rank2"]/item["allNum"]*100;
            })
            var r3 = data.slice(40,83).map(function (item) {
                return item["rank3"]/item["allNum"]*100;
            })
            var r4 = data.slice(40,83).map(function (item) {
                return item["rank4"]/item["allNum"]*100;
            })
            var option = {
                title: {
                    text: '不同年份国漫评分占比',
                    left: 'center',
                    top: 0,
                    textStyle: {
                        color: '#ffffff',
                        fontSize:14
                    }

                },
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                        type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                    }
                },
                legend: {
                    top:20,
                    textStyle:{
                        color: "rgba(255, 255, 255, 1)"
                    },

                    data: ['4分以下', '4-6分', '6-8分', '8分以上']
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: {
                    type: 'category',
                    data: year,
            axisLabel: {
                color: "rgba(255,255,255, 1)"
            }
                },
                yAxis: {
                    type: 'value',
                    max:100,
                    axisLabel: {
                        color: "rgba(255,255,255, 1)"
                    }
                },
                series: [
                    {
                        name: '4分以下',
                        type: 'bar',
                        stack: '总量',
                        label: {
                            show: false,
                            position: 'insideRight'
                        },
                        data: r1
                    },
                    {
                        name: '4-6分',
                        type: 'bar',
                        stack: '总量',
                        label: {
                            show: false,
                            position: 'insideRight'
                        },
                        data: r2
                    },
                    {
                        name: '6-8分',
                        type: 'bar',
                        stack: '总量',
                        label: {
                            show: false,
                            position: 'insideRight'
                        },
                        data: r3
                    },
                    {
                        name: '8分以上',
                        type: 'bar',
                        stack: '总量',
                        label: {
                            show: false,
                            position: 'insideRight'
                        },
                        data: r4
                    }
                ]
            };

            // 把配置给实例对象
            myChart.setOption(option);
            window.addEventListener("resize", function() {
                myChart.resize();
            });

        }})

})();
//1-3类型 折线图 类型数量的变化
(function() {
    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.querySelector(".type"));


    // (1)准备数据
    $.ajax({
        url:"/main/test2",
        type: "POST",
        success:function(data){
            //删除2020年后的
            var year = data.slice(0,83).map(function(item) {
                return item["year"];
            })
            var all = data.slice(0,83).map(function(item) {
                return item["num"];
            })
            // 2. 指定配置和数据
            var option = {
                color: ["#00f2f1"],
                tooltip: {
                    // 通过坐标轴来触发
                    trigger: "axis"
                },
                title: {
                    text: '国漫类型总量变化',
                    left: 'center',
                    top: 20,
                    textStyle: {
                        color: '#ffffff',
                        fontSize:14
                    }

                },
                legend: {
                    // 距离容器10%
                    right: "10%",
                    top:30,
                    // 修饰图例文字的颜色
                    textStyle: {
                        color: "#4c9bfd"
                    }
                },
                grid: {
                    top: "20%",
                    left: "3%",
                    right: "4%",
                    bottom: "3%",
                    show: true,
                    borderColor: "#012f4a",
                    containLabel: true
                },

                xAxis: {
                    type: "category",
                    boundaryGap: false,
                    data: year,
                    // 去除刻度
                    axisTick: {
                        show: true
                    },
                    // // 修饰刻度标签的颜色
                     axisLabel: {
                        color: "rgba(255,255,255,.7)"
                     },
                    // 去除x坐标轴的颜色
                    axisLine: {

                        show: true
                    }
                },
                yAxis: {
                    type: "value",
                    // 去除刻度
                    axisTick: {
                        show: false
                    },
                     // 修饰刻度标签的颜色
                     axisLabel: {
                        color: "rgba(255,255,255,.7)"
                     },
                    // 修改y轴分割线的颜色
                    splitLine: {
                        lineStyle: {
                            color: "#ffffff"
                        }
                    }
                },
                series: [
                    {
                        name: "标签种类",
                        type: "line",
                        // stack: "总量",
                        smooth: true,
                        data: all
                    }
                ]
            };

            // 重新把配置好的新数据给实例对象
            myChart.setOption(option);
            window.addEventListener("resize", function() {
                myChart.resize();
            });
        },
        error:function(XMLHttpRequest, textStatus,
                       errorThrown){
            alert("请求失败");
        }
    })


})();
//1-3.2 饼状图X3 类型占比
(function() {
    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.querySelector(".type1"));


    // (1)准备数据
    $.ajax({
        url:"/main/test3",
        type: "POST",
        success:function(data) {
            //删除2020年后的
            var type = data.slice(0,1).map(function (item) {
                return item["type"];
            })
            var num = data.slice(0,1).map(function (item) {
                return item["num"];
            })

            var servicedata=[];
            for(var i=0;i<type[0].length;i++){
                var obj=new Object();
                obj.name=type[0][i];
                obj.value=num[0][i];
                servicedata[i]=obj;
            }

            var option = {

                title: {
                    text: '1978以前',
                    left: 'center',
                    bottom:0,
                    textStyle: {
                        color: '#ccc'
                    }
                },
                tooltip: {
                    trigger: "item",
                    formatter: "{a} <br/>{b}: {c} ({d}%)",
                    position: function(p) {
                        //其中p为当前鼠标的位置
                        return [p[0] + 10, p[1] - 10];
                    }
                },
                legend: {
                    top: "90%",
                    itemWidth: 10,
                    itemHeight: 10,
                    data: type,
                    textStyle: {
                        color: "rgba(255,255,255,.5)",
                        fontSize: "12"
                    }
                },
                series: [{
                    name: "类型占比",
                    type: "pie",
                    center: ["50%", "42%"],
                    radius: ["40%", "60%"],
                    label: { show: false },
                    labelLine: { show: false },
                    data: servicedata
                }]
            };

            // 使用刚指定的配置项和数据显示图表。
            myChart.setOption(option);
            window.addEventListener("resize", function() {
                myChart.resize();
            });
        }})

})();
(function() {
    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.querySelector(".type2"));


    // (1)准备数据
    $.ajax({
        url:"/main/test3",
        type: "POST",
        success:function(data) {
            //删除2020年后的
            var type = data.slice(1,2).map(function (item) {
                return item["type"];
            })
            var num = data.slice(1,2).map(function (item) {
                return item["num"];
            })

            var servicedata=[];
            for(var i=0;i<type[0].length;i++){
                var obj=new Object();
                obj.name=type[0][i];
                obj.value=num[0][i];
                servicedata[i]=obj;
            }
            var option = {

                title: {
                    text: '1978-2003',
                    left: 'center',
                    bottom:0,
                    textStyle: {
                        color: '#ccc'
                    }
                },
                tooltip: {
                    trigger: "item",
                    formatter: "{a} <br/>{b}: {c} ({d}%)",
                    position: function(p) {
                        //其中p为当前鼠标的位置
                        return [p[0] + 10, p[1] - 10];
                    }
                },
                legend: {
                    top: "90%",
                    itemWidth: 10,
                    itemHeight: 10,
                    data: type,
                    textStyle: {
                        color: "rgba(255,255,255,.5)",
                        fontSize: "12"
                    }
                },
                series: [{
                    name: "类型占比",
                    type: "pie",
                    center: ["50%", "42%"],
                    radius: ["40%", "60%"],
                    label: { show: false },
                    labelLine: { show: false },
                    data: servicedata
                }]
            };

            // 使用刚指定的配置项和数据显示图表。
            myChart.setOption(option);
            window.addEventListener("resize", function() {
                myChart.resize();
            });
        }})

})();
(function() {
    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.querySelector(".type3"));


    // (1)准备数据
    $.ajax({
        url:"/main/test3",
        type: "POST",
        success:function(data) {

            var type = data.slice(2,3).map(function (item) {
                return item["type"];
            })
            var num = data.slice(2,3).map(function (item) {
                return item["num"];
            })

            var servicedata=[];
            for(var i=0;i<type[0].length;i++){
                var obj=new Object();
                obj.name=type[0][i];
                obj.value=num[0][i];
                servicedata[i]=obj;
            }
            var option = {

                title: {
                    text: '2003至今',
                    left: 'center',
                    bottom:0,
                    textStyle: {
                        color: '#ccc'
                    }
                },
                tooltip: {
                    trigger: "item",
                    formatter: "{a} <br/>{b}: {c} ({d}%)",
                    position: function(p) {
                        //其中p为当前鼠标的位置
                        return [p[0] + 10, p[1] - 10];
                    }
                },
                legend: {
                    top: "90%",
                    itemWidth: 10,
                    itemHeight: 10,
                    data: type,
                    textStyle: {
                        color: "rgba(255,255,255,.5)",
                        fontSize: "12"
                    }
                },
                series: [{
                    name: "类型占比",
                    type: "pie",
                    center: ["50%", "42%"],
                    radius: ["40%", "60%"],
                    label: { show: false },
                    labelLine: { show: false },
                    data: servicedata
                }]
            };

            // 使用刚指定的配置项和数据显示图表。
            myChart.setOption(option);
            window.addEventListener("resize", function() {
                myChart.resize();
            });
        }})

})();
//1-4 合拍发展 条形图
(function() {
    // 实例化对象
    var myChart = echarts.init(document.querySelector(".co"));
    // 指定配置和数据
    $.ajax({
        url:"/main/test1",
        type: "POST",
        success:function(data) {
            //删除2020年后的
            var year = data.map(function (item) {
                return item["year"];
            })
            var len1 = data.map(function (item) {
                return item["num"];
            })
            var option = {
                grid: {
                    left: "0%",
                    top: "10px",
                    right: "0%",
                    bottom: "4%",
                    container: true
                },
                title: {
                    text: '每年中外合拍数量',
                    left: 'center',
                    top: 10,
                    textStyle: {
                        color: '#ffffff',
                        fontSize:14

                    }

                },
                tooltip: {
                    trigger: 'axis'
                },
                calculable: true,
                xAxis: [{
                    type: 'category',
                    data: year,
                    axisLabel: {
                        color: "rgba(255, 255, 255, 1)",
                        fontWeight: "bold"
                    }
                }],
                yAxis: [{
                    type: 'value',
                    axisTick: {
                        show: true
                    },

                    axisLabel: {
                        color: "rgba(255, 255, 255, 1)",
                        fontWeight: "bold",
                        show:true
                    },
                    max:110
                }],
                series: [
                    {
                        name: '合拍数量',
                        type: 'bar',
                        data: len1,
                    }
                ]
            };

            // 把配置给实例对象
            myChart.setOption(option);
            window.addEventListener("resize", function() {
                myChart.resize();
            });

        }})

})();
//1-4.2 条形图 各国合拍数量
(function() {
    // 实例化对象
    var myChart = echarts.init(document.querySelector(".place"));
    // 指定配置和数据
    $.ajax({
        url:"/main/test4",
        type: "POST",
        success:function(data) {
            //删除2020年后的

            var place= data.map(function (item) {
                return item["place"]
            })
            var num= data.map(function (item) {
                return item["num"];
            })
            var option = {
                grid: {
                    left: "0%",
                    top: "10px",
                    right: "0%",
                    bottom: "4%",
                    container: true
                },
                title: {
                    text: '与各国合拍数量',
                    left: 'center',
                    top: 10,
                    textStyle: {
                        color: '#ffffff',
                        fontSize:14
                    }

                },
                tooltip: {
                    trigger: 'axis'
                },
                calculable: true,
                xAxis: [{
                    type: 'category',
                    data: place,
                    axisLabel: {
                        show: true,
                        color:"rgba(255,255,255,1)",
                        fontWeight: "bold"
                    }

                }],
                yAxis: [{
                    type: 'value',
                    axisTick: {
                        show: true,
                    },
                    max:50
                }],
                series: [{
                    name: '数量',
                    type: 'bar',
                    data: num
                }
                ]
            };

            // 把配置给实例对象
            myChart.setOption(option);
            window.addEventListener("resize", function() {
                myChart.resize();
            });

        }})

})();

//2-1 条形图 看过人数变化
(function() {
    // 实例化对象
    var myChart = echarts.init(document.querySelector(".view"));
    // 指定配置和数据
    $.ajax({
        url:"/main/now",
        type: "POST",
        success:function(data) {
            var year = data.map(function (item) {
                return item["year"];
            })
            var len1 = data.map(function (item) {
                return item["view_num"];
            })
            var option = {
                grid: {
                    left: "0%",
                    top: "10px",
                    right: "0%",
                    bottom: "4%",
                    container: true
                },
                title: {
                    text: '每年看过人数数量',
                    left: 'center',
                    top: 30,
                    textStyle: {
                        color: '#ffffff',
                        fontSize:14
                    }

                },
                tooltip: {
                    trigger: 'axis'
                },
                legend: {
                    data: ['']
                },

                calculable: true,
                xAxis: [{
                    type: 'category',
                    data: year,
                        axisLabel: {
                            show: true,
                            color: "rgba(255, 255, 255, 1)"
                        }

                }],
                yAxis: [{
                    type: 'value',
                    axisTick: {
                        show: true
                    },
                    axisLabel: {
                        show: true,
                        color: "rgba(255, 255, 255, 0.01)"
                    },
                    max:3000000
                }],
                series: [
                    {
                        name: '看过人数',
                        type: 'bar',
                        data: len1
                    }
                ]
            };

            // 把配置给实例对象
            myChart.setOption(option);
            window.addEventListener("resize", function() {
                myChart.resize();
            });

            // 数据变化
            var dataAll = [
                { year: "2019", data: [200, 300, 300, 900, 1500, 1200, 600] },
                { year: "2020", data: [300, 400, 350, 800, 1800, 1400, 700] }
            ];

            $(".bar h2 ").on("click", "a", function() {
                option.series[0].data = dataAll[$(this).index()].data;
                myChart.setOption(option);
            });
        }})

})();
//2-2 折线图 评论占看过比例变化
(function() {
    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.querySelector(".rate"));


    // (1)准备数据
    $.ajax({
        url:"/main/now",
        type: "POST",
        success:function(data){
            //删除2020年后的
            var year = data.slice(67,82).map(function(item) {
                return item["year"];
            })
            var all = data.slice(67,82).map(function(item) {
                return item["com_num"]/item["view_num"];
            })
            // 2. 指定配置和数据
            var option = {
                color: ["#00f2f1"],
                tooltip: {
                    // 通过坐标轴来触发
                    trigger: "axis"
                },
                title: {
                    text: '2005年起评论占看过人数比例',
                    left: 'center',
                    top: 20,
                    textStyle: {
                        color: '#ffffff',
                        fontSize:14
                    }

                },
                legend: {
                    // 距离容器10%
                    right: "10%",
                    top:35,
                    // 修饰图例文字的颜色
                    textStyle: {
                        color: "#4c9bfd"
                    }
                },
                grid: {
                    top: "20%",
                    left: "3%",
                    right: "4%",
                    bottom: "3%",
                    show: true,
                    borderColor: "#012f4a",
                    containLabel: true
                },

                xAxis: {
                    type: "category",
                    boundaryGap: false,
                    data: year,
                    // 去除刻度
                    axisTick: {
                        show: false
                    },
                    // 修饰刻度标签的颜色
                    axisLabel: {
                        color: "rgba(255,255,255,1)",
                        fontWeight:"bold"
                    },
                    // 去除x坐标轴的颜色
                    axisLine: {

                        show: true
                    }
                },
                yAxis: {
                    type: "value",
                    // 去除刻度
                    axisTick: {
                        show: false
                    },
                    // 修饰刻度标签的颜色
                    axisLabel: {
                        color: "rgba(255,255,255,1)",
                        fontWeight:"bold"
                    },
                    // 修改y轴分割线的颜色
                    splitLine: {
                        lineStyle: {
                            color: "ffffff"
                        }
                    }
                },
                series: [
                    {
                        name: "评论占比",
                        type: "line",
                        // stack: "总量",
                        smooth: true,
                        data: all
                    }
                ]
            };

            // 重新把配置好的新数据给实例对象
            myChart.setOption(option);
            window.addEventListener("resize", function() {
                myChart.resize();
            });
        },
        error:function(XMLHttpRequest, textStatus,
                       errorThrown){
            alert("请求失败");
        }
    })


})();
