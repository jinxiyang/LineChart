# LineChart


##表的样式参数

* 坐标轴字体颜色、字体大小
* x轴颜色、高度
* 背景虚线的颜色、高度
* 阴影的颜色、透明度、l,t,r,b
* 点的颜色、点的状态(正常、动画状态)
* 点点连线的颜色、宽度
* maker的背景颜色、字体大小、颜色
* maker line的颜色、宽度
* 两点之间动画执行时间


##数据类型
表中点的数据：

* xValue
* xLabel
* yValue
* yLabel
* x
* y

实际数据，如：

* datestr
* num

##动画效果
* 第一个点闪烁，100ms停止，下一个点开始闪烁，此时连线从第一个点发出，100ms后连到第二个点
* 第二个点停止闪烁，此时连线从第二个点发出，100ms后连到第三个点
* 之后以此类推，直到连上最后一个点
* 在连线前进时，下面阴影区同步移动

##数据处理
* 从后台获取到数据时，遍历出最大的y值
* 以适当方式求出y轴一个单位距离所代表的数字，且是合适的，比如：5000，1万，5万，10万，50万，100万……
* 数据是连续十天的门诊量，x轴间距是均匀的
* 求出十个点的坐标

##绘制表
* 表的测量，有样图可知宽高是给定的，故不需测量宽高