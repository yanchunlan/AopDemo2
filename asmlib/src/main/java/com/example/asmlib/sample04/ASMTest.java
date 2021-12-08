package com.example.asmlib.sample04;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * author:  ycl
 * date:  2019/11/18 16:11
 * desc:
 */
public class ASMTest {
    public static void main(String[] args) throws Exception {
        File file = new File("asmlib/build/classes/java/main/com/example/asmlib/sample04/Base.class");
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

        protected MyMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc, String className) {
            super(api, mv, access, name, desc);
            this.className = className;
            this.methodName = name;
            System.out.println("MyMethodVisitor className: "+ className+" method: "+methodName+" name: "+  name);
        }

        private final Label tryStart = new Label();
        private final Label tryEnd = new Label();
        private final Label catchStart = new Label();
        private final Label catchEnd = new Label();

        @Override
        protected void onMethodEnter() {
            super.onMethodEnter();
            if (isTryCatch()) {
                mv.visitTryCatchBlock(tryStart, tryEnd, catchStart, "java/lang/Exception");
                mv.visitLabel(tryStart);
            }
        }

        @Override
        protected void onMethodExit(int opcode) {
            super.onMethodExit(opcode);
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
            return className.equals("com/example/asmlib/sample04/Base") && methodName.equals("tryCatch");
        }
    }

}
