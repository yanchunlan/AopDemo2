# AutoTryCatchDemo
Javassist，ASM的使用

- javassist
    
    1.  ClassPool 字节码容器池
    2.  ClassPool 缓存字节码对象的容器，所有的Class字节码对象都在ClassPool中
    3.  CtClass
      对象很多时，ClassPool会消耗很大内存，为了避免内存消耗，构建对象时可使用单列模式
    4.  对于CtClass 对象，调用deatch方法将其从ClassPool移除
    
- asm

  1.  AS使用 `Asm Bytecode Outline` 插件查看字节码
  2.  

    
