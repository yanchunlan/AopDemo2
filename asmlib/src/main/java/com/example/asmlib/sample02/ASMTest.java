package com.example.asmlib.sample02;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * author:  ycl
 * date:  2019/11/18 16:11
 * desc:
 */
public class ASMTest {
    public static void main(String[] args) throws Exception {
        File file = new File("asmlib/build/classes/java/main/com/example/asmlib/sample01/Base.class");
        FileInputStream fileInputStream = new FileInputStream(file);

        ClassReader cr = new ClassReader(fileInputStream);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        ClassVisitor cv = new MYClassVisitor(cw);
        cr.accept(cv, ClassReader.SKIP_DEBUG);
        byte[] data = cw.toByteArray();

        FileOutputStream fos = new FileOutputStream(file.getParentFile().getAbsolutePath() + File.separator + "Base.class");
        fos.write(data);
        fos.close();
        System.out.println(" success ");
    }

    static class MYClassVisitor extends ClassVisitor {
        public MYClassVisitor(  ClassVisitor cv) {
            super(Opcodes.ASM7, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals("<init>")) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            return new MyMethodVisitor(api,mv,access,name,desc);
        }
    }

    static class MyMethodVisitor extends AdviceAdapter {

        protected MyMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc) {
            super(api, mv, access, name, desc);
        }

        @Override
        protected void onMethodEnter() {
            super.onMethodEnter();
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("start");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        }

        @Override
        protected void onMethodExit(int opcode) {
            super.onMethodExit(opcode);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("end");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
    }

}
