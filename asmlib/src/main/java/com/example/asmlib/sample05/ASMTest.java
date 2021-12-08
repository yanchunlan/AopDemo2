package com.example.asmlib.sample05;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ListIterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * author:  ycl
 * date:  2019/11/18 16:11
 * desc:
 */
public class ASMTest {
    public static void main(String[] args) throws Exception {
        File file = new File("asmlib/build/classes/java/main/com/example/asmlib/sample05/ThreadTest.class");
        FileInputStream fileInputStream = new FileInputStream(file);

        ClassReader cr = new ClassReader(fileInputStream);
        // tree api 实质上是coreApi的拓展
        MyClassNode cn = new MyClassNode(Opcodes.ASM7); // classNode extend classVisit
        cr.accept(cn, 0);
        cn.updateMethod();

        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        byte[] data = cw.toByteArray();

        FileOutputStream fos = new FileOutputStream(file.getParentFile().getAbsolutePath() + File.separator + "Base.class");
        fos.write(data);
        fos.close();
        System.out.println(" success ");
    }


    static class MyClassNode extends ClassNode {

        public MyClassNode(int api) {
            super(api);
        }


        // 1. CoreApi方式实现类替换
//        @Override
//        public MethodVisitor visitMethod(int access, String name, String desc, String signature,
//            String[] exceptions) {
//            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
//            return new MyMethodVisitor(mv);
//        }

        // 2. TreeApi方式实现类替换
        public void updateMethod() {
            for (MethodNode methodNode : methods) {
                ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == Opcodes.INVOKESTATIC) {
                        if (node instanceof MethodInsnNode) {
                            hookExecutors(((MethodInsnNode) node));
                        }
                    }
                }
            }
        }

        private void hookExecutors(MethodInsnNode methodNode) {
            if (methodNode.owner.equals("java/util/concurrent/Executors")) {

                if (methodNode.name.equals("newFixedThreadPool")) {
                    methodNode.owner = "com/example/asmlib/sample05/ThreadTest$MyThreadPoolExecutor";
                    methodNode.name = "getThreadPool";

                    String desc = "(I)Ljava/util/concurrent/ExecutorService;";
                    int index = desc.lastIndexOf(")");
                    String result = desc.substring(0, index + 1) +
                        "Lcom/example/asmlib/sample05/ThreadTest$MyThreadPoolExecutor;";
                    methodNode.desc = result;
                }
            }
        }
    }


    static class MyMethodVisitor extends MethodVisitor{

        public MyMethodVisitor( MethodVisitor methodVisitor) {
            super(Opcodes.ASM7, methodVisitor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor,
            boolean isInterface) {

            String desc = "(I)Ljava/util/concurrent/ExecutorService;";

            if (owner.equals("java/util/concurrent/Executors") &&
                name.equals("newFixedThreadPool") &&
                desc.equals(desc)) {

                int index = desc.lastIndexOf(")");
                String result = desc.substring(0, index + 1) +
                    "Lcom/example/asmlib/sample05/ThreadTest$MyThreadPoolExecutor;";

                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "com/example/asmlib/sample05/ThreadTest$MyThreadPoolExecutor",
                    "getThreadPool",
                    result,
                    isInterface);
            } else {
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }
        }
    }
}
