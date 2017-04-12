
detection_data表
detection1：温度 (T)
detection2：PH
detection3：ORP
detection4：液位计1(LS_state)
detection5：DO
detection6：流量 数字？？(没有电磁流量计时的FLow) //按天对detection求和得日处理水量
detection7：LS_state_two （液位计2）
detection8：电磁流量计瞬时流量 (actureFlow)
detection9：电磁流量计总流量  (sumFlow)


说明detection(1-9):以上是原来的方式测出来的数据。


detection10：COD 化学需氧量
detection11：AN 氨氮
detection12：FTU 浊度
detection13：TP 总磷
detection14：RH RH

说明detection(10-14):点创设备测出的数据,未安装点创设备的站点无此数据 （字段都为double型，小数点保留2位）

detection15：备用
detection16：备用
detection17：备用
detection18：备用
detection19：备用
detection20：备用


=======================================================================================





run_data表

该表如果没有数据设置成 null

equipment1name=
equipment2name=
equipment3name=
equipment4name=
equipment5name=

说明detection(1-5): 原来的方式测出的设备状态（具体的含义要看控制方式）,在连接PLC设备时，这些字段数据值为空。


equipment6name= 液位计1 （提升池液位） 
equipment7name= 液位计2 （原水池液位）
equipment8name= 提升泵1
equipment9name= 提升泵2
equipment10name= 原水泵1
equipment11name= 原水泵2
equipment12name= 风机1
equipment13name= 风机2
equipment14name= 混合液回流泵1
equipment15name= 混合液回流泵2
equipment16name= 污泥回流泵1
equipment17name= 污泥回流泵2
equipment18name= 消毒机
equipment19name= 电磁阀1
equipment20name= 电磁阀2
equipment21name= 电磁阀3

说明detection（6-21）：PLC设备测出的数据，在没有安装PLC设备时，（6-21）数据为空。



equipment22name=备用
equipment23name=备用
equipment24name=备用
equipment25name=备用



**********************************
地图popoverlay上显示的东西
sewage表
站点名  short_title
运营编号 operationnum


表run_data
equipment6state= 液位计1 （提升池液位）
equipment7name= 液位计2 （原水池液位）
equipment8name= 提升泵1
equipment9name= 提升泵2
equipment10name= 原水泵1
equipment11name= 原水泵2
equipment12name= 风机1
equipment13name= 风机2
equipment14name= 混合液回流泵1
equipment15name= 混合液回流泵2
equipment16name= 污泥回流泵1
equipment17name= 污泥回流泵2
equipment18name= 消毒机
equipment19name= 电磁阀1
equipment20name= 电磁阀2
equipment21name= 电磁阀3

detection_data表
detection8：电磁流量计瞬时流量 (actureFlow) 瞬时流量
detection6:按照天求和  日处理水量
