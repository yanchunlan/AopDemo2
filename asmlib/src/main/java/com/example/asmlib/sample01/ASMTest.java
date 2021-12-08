package com.example.asmlib.sample01;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * author:  ycl
 * date:  2019/11/08 16:38
 * desc:
 */
public class ASMTest {
    public static void main(String[] args) throws IOException {

        ClassReader cr = new ClassReader("com/example/asmlib/sample01/Base");
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        ClassVisitor cv = new MYClassVisitor(cw);
        cr.accept(cv, ClassReader.SKIP_DEBUG);
        byte[] data = cw.toByteArray();

        File f = new File("asmlib/build/classes/java/main/com/example/asmlib/sample01/Base.class");
        FileOutputStream fout = new FileOutputStream(f);
        fout.write(data);
        fout.close();
        System.out.println(" success ");

    }


    static class MYClassVisitor extends ClassVisitor implements Opcodes {

        public MYClassVisitor(ClassVisitor cv) {
            super(Opcodes.ASM5, cv);
        }


        @Override
        public void visit(int version, int access, String name, String signature,
                          String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
//            System.out.println("MYClassVisitor visit version: "+version+" access： "+access+" name： "+name+" signature： "+signature+" superName： "+superName+" interfaces： "+interfaces.toString());
        }


        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//            System.out.println("MYClassVisitor visitMethod access： "+access+" name： "+name+" signature： "+signature+" exceptions： "+exceptions);
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("process") && mv != null) {
                mv = new MyMethodVisitor(mv);
            }
            return mv;
        }
    }

    static class MyMethodVisitor extends MethodVisitor implements Opcodes {

        public MyMethodVisitor(MethodVisitor methodVisitor) {
            super(Opcodes.ASM5, methodVisitor);
        }

        @Override
        public void visitCode() {
            super.visitCode();
//            System.out.println("MyMethodVisitor visitCode");

            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("start");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        }


        /**
         *  访问到无参数指令 调用此方法
         *  如果是return 就在其前面添加一条指令
         *
         * @param opcode
         */
        @Override
        public void visitInsn(int opcode) {
            super.visitInsn(opcode);
//            System.out.println("MyMethodVisitor visitInsn opcode : "+opcode);

            if ((opcode>=Opcodes.IRETURN &&opcode <= Opcodes.RETURN)||opcode==Opcodes.ATHROW) {
                // 返回之前，打印 end
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitLdcInsn("end");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            }
            mv.visitInsn(opcode);
        }
    }
}

