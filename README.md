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

  1.  AS使用 `Asm Bytecode Outline` 插件查看字节码
  2.  ClassVisitor 解析类，再使用其对应的xxxVisitor解析其他
  3.  在对应的xxxVisitor中插入字节码

- 总结：
    1.  ASM 比 jaassist 占据内存更小，更快
    2.  ASM提供更多精细化的字节码操作
    3.  javassist会生成很多临时变量
    
