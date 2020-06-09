package com.example.asmlib.sample03;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
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
        File file = new File("asmlib/build/classes/java/main/com/example/asmlib/sample03/Base.class");
        FileInputStream fileInputStream = new FileInputStream(file);

        ClassReader cr = new ClassReader(fileInputStream);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        ClassVisitor cv = new MYClassVisitor(cw);
        cr.accept(cv, ClassReader.SKIP_DEBUG);
        byte[] data = cw.toByteArray();

        FileOutputStream fos = new FileOutputStream(file.getParentFile().getParentFile().getAbsolutePath() + File.separator + "Base.class");
        fos.write(data);
        fos.close();
        System.out.println(" success ");
    }

    static class MYClassVisitor extends ClassVisitor {
        private String className;

        public MYClassVisitor(ClassVisitor cv) {
            super(Opcodes.ASM7, cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            this.className = name;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            return new MyMethodVisitor(api, mv, access, name, desc, className);
        }
    }

    static class MyMethodVisitor extends AdviceAdapter {

        private final String methodName;
        private final String className;
        private boolean find = false;

        protected MyMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc, String className) {
            super(api, mv, access, name, desc);
            this.className = className;
            this.methodName = name;
            System.out.println("MyMethodVisitor className: "+ className+" method: "+methodName+" name: "+  name);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            super.visitTypeInsn(opcode, type);
            System.out.println("visitTypeInsn opcode " + opcode + " type " + type);
//            if (opcode == Opcodes.NEW && "java/lang/Thread".equals(type)) {
//                find = true;
//                mv.visitTypeInsn(Opcodes.NEW, "com/example/asmlib/sample03/MyThread");
//                System.out.println("visitTypeInsn find true");
//                return;
//            }
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            System.out.println("visitMethodInsn opcode " + opcode + " owner " + owner + " name " + name + " desc " + desc);

           // ########################  2.  判断哪里调用了指定方法 ####################
            if (owner.equals("com/example/asmlib/sample03/Base$BThread") && name.equals("returnString") && desc.equals("()Ljava/lang/String;")) {
                System.out.println("visitMethodInsn returnString className: "+ className+" method: "+methodName+" name: "+  name);
                System.out.println("########### 2 return ##########");
            }
            // ########################  2.  判断哪里调用了指定方法 ####################

//            if ("java/lang/Thread".equals(owner)
//                    && !className.equals("com/example/asmlib/sample03/MyThread")
//                    && opcode == Opcodes.INVOKESPECIAL
//                    && find
//            ) {
//                find = false;
//                mv.visitMethodInsn(opcode, "com/example/asmlib/sample03/MyThread", name, desc, itf);
//                System.out.println("visitMethodInsn className: "+ className+" method: "+methodName+" name: "+  name);
//                return;
//            }
        }


        // ########################  3.  tryCatch  ####################
        private final Label tryStart = new Label();
        private final Label tryEnd = new Label();
        private final Label catchStart = new Label();
        private final Label catchEnd = new Label();

        @Override
        public void monitorEnter() {
            super.monitorEnter();

            if (isTryCatch()) {
                mv.visitTryCatchBlock(tryStart, tryEnd, catchStart, "java/lang/Exception");
                mv.visitLabel(tryStart);
            }
        }

        @Override
        public void monitorExit() {
            super.monitorExit();
            if (isTryCatch()) {
                mv.visitLabel(tryEnd);
                mv.visitJumpInsn(GOTO, catchEnd);
                mv.visitLabel(catchStart);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/RuntimeException", "printStackTrace", "()V", false);
                mv.visitInsn(Opcodes.RETURN);
                mv.visitLabel(catchEnd);
            }
        }

        private boolean isTryCatch() {
            return className.equals("com/example/asmlib/sample03/Base") && methodName.equals("tryCatch");
        }
        // ########################  3.  tryCatch  ####################

    }

}
