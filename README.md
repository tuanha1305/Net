## 缓存

缓存函数设计

读取网络/缓存 -> 失败 -> 读取网络/缓存 -> 成功 -> 写缓存是/否

总结为三步



链式函数

```
emnu Cache {
	Cache, Netwok
}

prior(Netwok).fail(Cache).write()
```



重载函数

```
emnu Cache {
	Http, Cache, Netwok, Write
}

Cache + Netwok + Write  // 缓存 -> 失败 -> 网络 -> 写缓存
Netwok + Cache + Write // 网络 -> 失败 -> 缓存 -> 写缓存
Cache // 只读缓存
Network + Write // 只读网络 -> 写缓存
Http + Http + Http


第一个为首先
第二个为失败后
第三个为成功后
```



## 转换器

JSON

- 解析数组



文件转换

- File
- Uri
- String



### 下载

进度监听

- 上传
- 下载
  - 当前字节
  - 总共字节
  - 剩余字节
  - 每秒字节
  - 百分比进度
  - 是否完成
- 通知栏进度



多任务下载

- 所有任务的进度
- 每个任务进度



下载策略

- 极速下载(MD5校验)
- 文件名称冲突策略
- 下载后安装

