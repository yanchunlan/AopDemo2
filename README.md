# AutoTryCatchDemo
Javassist，ASM的使用

- javassist
    
    1.  ClassPool 字节码容器池
    2.  ClassPool 缓存字节码对象的容器，所有的Class字节码对象都在ClassPool中
    3.  CtClass
      对象很多时，ClassPool会消耗很大内存，为了避免内存消耗，构建对象时可使用单列模式
    4.  对于CtClass 对象，调用deatch方法将其从ClassPool移除
    5.  CtClass 里面包含CtField，CtConst,CtMethod
    6.  insertBefore ,insertAfter，insertAt，addCatch是其支持的属性
    7.  主要是以字符串的方式添加新的代码
    
- asm

  1.  AS使用 `Asm Bytecode Outline`/`jclasslib Bytecode viewer Ingo
      Kegel`插件查看字节码
      - Asm Bytecode Outline 使用是点击java文件，右键`show byteCode
        onlin`,右方出现弹框显示
      - jclasslib Bytecode viewer IngoKegel 使用菜单栏`View`中选择`Show
        Bytecode With jclasslib`
  2.  ClassVisitor 解析类，再使用其对应的xxxVisitor解析其他
  3.  在对应的xxxVisitor中插入字节码

- 总结：
    1.  ASM 比 javassist 占据内存更小，更快
    2.  ASM提供更多精细化的字节码操作
    3.  javassist会生成很多临时变量

- 操作文档 ：
  [字节码总结笔记](https://github.com/yanchunlan/SourceCodeSummary/blob/master/%E6%80%A7%E8%83%BD%E4%BC%98%E5%8C%96/%E6%9E%81%E8%87%B4%E6%80%A7%E8%83%BD%E4%BC%98%E5%8C%96%E6%80%BB%E7%BB%93/%E5%AD%97%E8%8A%82%E7%A0%81%E6%93%8D%E4%BD%9C.txt)
  
##### demo 中的案列解释 ：
- app： 
  -    build.gradle中校验了2个插件 'clicklistener'，'method-time-trace'

- asmlib： 

  -    sample01: asm生成文件的demo，在方法开始，结束插入log
  -    methodtime: 每个方法中开始，结束，打印指定的插桩方法
    
- javassistlib： 

  -   javassist: 拦截点击事件打印toast
  -   agent: 运行时，javassist插桩
  -   sample01: javassist生成文件的demo，在方法开始，结束插入log
    
    
    
 
    
