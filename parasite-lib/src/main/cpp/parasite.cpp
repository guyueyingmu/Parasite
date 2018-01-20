#include <map>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>

#include "parasite.h"

using namespace std;

JNIHIDDEN const char *g_className = "com/hu/parasite/hook/HookManager";
JNIHIDDEN const char *g_modelName = "com/hu/parasite/hook/HookManager$Methods";

JNIHIDDEN void JNICALL nativeHook(JNIEnv *env, jobject thiz, jmethod origin, jmethod hook);
JNIHIDDEN void JNICALL nativeUnhook(JNIEnv *env, jobject thiz, jmethod method, jboolean cache);

JNIHIDDEN static JNINativeMethod g_nativeMethods[] = {
        { "nativeHook", "(Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)V", (void *) nativeHook },
        { "nativeUnhook", "(Ljava/lang/reflect/Method;Z)V", (void *) nativeUnhook },
};

JNIHIDDEN size_t g_methodSize = 0;
JNIHIDDEN map<jmethodID, Method> g_methodMap;

JNIHIDDEN jint initParasite(JNIEnv *env)
{
    // 计算method结构体大小
    jclass modelClass = env->FindClass(g_modelName);
    size_t f1 = (size_t) env->GetStaticMethodID(modelClass, "f1", "(II)I");
    size_t f2 = (size_t) env->GetStaticMethodID(modelClass, "f2", "(IIII)J");
    g_methodSize = f2 - f1;

    LOGD(TAG, "size of method struct: %p", (void*) g_methodSize);

    return JNI_OK;
}

JNIHIDDEN void JNICALL nativeHook(JNIEnv *env, jobject thiz, jmethod origin, jmethod hook)
{
    jmethodID oldMethodID = env->FromReflectedMethod(origin);
    jmethodID newMethodID = env->FromReflectedMethod(hook);

    // 保存原来的method并替换整个method
    map<jmethodID, Method>::iterator iter = g_methodMap.find(oldMethodID);
    if (iter == g_methodMap.end()) {
        Method method = malloc(g_methodSize);
        memcpy(method, oldMethodID, g_methodSize);
        g_methodMap.insert(pair<jmethodID, Method>(oldMethodID, method));
    }
    memcpy(oldMethodID, newMethodID, g_methodSize);

    LOGD(TAG, "hook method %p --> %p", oldMethodID, newMethodID);
}

JNIHIDDEN void JNICALL nativeUnhook(JNIEnv *env, jobject thiz, jmethod method, jboolean cache)
{
    jmethodID methodID = env->FromReflectedMethod(method);

    // 还原原来的结构体
    map<jmethodID, Method>::iterator iter = g_methodMap.find(methodID);
    if (iter != g_methodMap.end()) {
        memcpy(methodID, iter->second, g_methodSize);
        if (!cache) {
            g_methodMap.erase(iter);
            free(iter->second);
        }
    }

    LOGD(TAG, "unhook method %p", methodID);
}

JNIHIDDEN jint registerNatives(JNIEnv* env)
{
    jint ret = JNIRegisterNatives(
            env,
            g_className,
            g_nativeMethods,
            COUNT_OF(g_nativeMethods));

    return ret;
}

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        return JNI_ERR;
    }

    if (registerNatives(env) != JNI_OK) {
        return JNI_ERR;
    }

    if (initParasite(env) != JNI_OK) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_4;
}