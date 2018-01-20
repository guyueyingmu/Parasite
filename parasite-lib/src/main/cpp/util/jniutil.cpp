#include "jniutil.h"
#include <stdio.h>

jint JNIRegisterNatives(JNIEnv* env, const char* clazz, JNINativeMethod* methods, jint nMethods) {
    jclass cls = env->FindClass(clazz);
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
        return JNI_ERR;
    }
    return env->RegisterNatives(cls, methods, nMethods);
    if (env != JNI_OK) {
        return JNI_ERR;
    }
    return JNI_OK;
}

jmethodID JNIGetMethodID(JNIEnv *env, const char* clazz, const char *method, const char* sig) {
    jclass cls = env->FindClass(clazz);
    if (env->ExceptionCheck()) {
        return NULL;
    }
    jmethodID methodID = env->GetMethodID(cls, method, sig);
    if (env->ExceptionCheck()) {
        return NULL;
    }
    return methodID;
}

jmethodID JNIGetStaticMethodID(JNIEnv *env, const char* clazz, const char *method, const char* sig) {
    jclass cls = env->FindClass(clazz);
    if (env->ExceptionCheck()) {
        return NULL;
    }
    jmethodID methodID = env->GetStaticMethodID(cls, method, sig);
    if (env->ExceptionCheck()) {
        return NULL;
    }
    return methodID;
}

jfieldID JNIGetFieldID(JNIEnv* env, const char* clazz, const char* field, const char* sig) {
    jclass cls = env->FindClass(clazz);
    if (env->ExceptionCheck()) {
        return NULL;
    }
    jfieldID fieldID = env->GetFieldID(cls, field, sig);
    if (env->ExceptionCheck()) {
        return NULL;
    }
    return fieldID;
}

jfieldID GetStaticFieldID(JNIEnv* env, const char* clazz, const char* field, const char* sig) {
    jclass cls = env->FindClass(clazz);
    if (env->ExceptionCheck()) {
        return NULL;
    }
    jfieldID fieldID = env->GetStaticFieldID(cls, field, sig);
    if (env->ExceptionCheck()) {
        return NULL;
    }
    return fieldID;
}

jobject JNINewObject(JNIEnv *env, const char* clazz, const char* sig, ...) {
    jclass cls = env->FindClass(clazz);
    if (env->ExceptionCheck()) {
        return NULL;
    }
    jmethodID methodID = env->GetMethodID(cls, "<init>", sig);
    if (env->ExceptionCheck()) {
        return NULL;
    }
    va_list args;
    va_start(args, sig);
    jobject obj = env->NewObject(cls, methodID, args);
    va_end(args);
    if (env->ExceptionCheck()) {
        return NULL;
    }
    return obj;
}

jobject JNINewObjectArray(JNIEnv *env, const char* clazz, jsize length, jobject initial) {
    jclass cls = env->FindClass(clazz);
    if (env->ExceptionCheck()) {
        return NULL;
    }
    jobject obj = env->NewObjectArray(length, cls, initial);
    if (env->ExceptionCheck()) {
        return NULL;
    }
    return obj;
}

void JNICallVoidMethod(JNIEnv *env, const char* clazz, const char *method, const char* sig, jobject obj, ...) {
    jclass cls = env->FindClass(clazz);
    if (env->ExceptionCheck()) {
        return;
    }
    jmethodID methodID = env->GetMethodID(cls, method, sig);
    if (env->ExceptionCheck()) {
        return;
    }
    va_list args;
    va_start(args, obj);
    env->CallObjectMethodV(obj, methodID, args);
    va_end(args);
    if (env->ExceptionCheck()) {
        return;
    }
}

#define JNI_CALL_TYPE_METHOD(type, name) \
type JNICall##name##Method(JNIEnv *env, const char* clazz, const char *method, const char* sig, jobject obj, ...) { \
    jclass cls = env->FindClass(clazz); \
    if (env->ExceptionCheck()) { \
        return (type) 0; \
    } \
    jmethodID methodID = env->GetMethodID(cls, method, sig); \
    if (env->ExceptionCheck()) { \
        return (type) 0; \
    } \
    va_list args; \
    va_start(args, obj); \
    type result = env->Call##name##MethodV(obj, methodID, args); \
    if (env->ExceptionCheck()) { \
        return (type) 0; \
    } \
    va_end(args); \
    return result; \
}

JNI_CALL_TYPE_METHOD(jobject, Object)
JNI_CALL_TYPE_METHOD(jboolean, Boolean)
JNI_CALL_TYPE_METHOD(jbyte, Byte)
JNI_CALL_TYPE_METHOD(jchar, Char)
JNI_CALL_TYPE_METHOD(jshort, Short)
JNI_CALL_TYPE_METHOD(jint, Int)
JNI_CALL_TYPE_METHOD(jlong, Long)
JNI_CALL_TYPE_METHOD(jfloat, Float)
JNI_CALL_TYPE_METHOD(jdouble, Double)

void JNICallNonvirtualVoidMethod(JNIEnv *env, const char* clazz, const char *method, const char* sig, jobject obj, ...) {
    jclass cls = env->FindClass(clazz);
    if (env->ExceptionCheck()) {
        return;
    }
    jmethodID methodID = env->GetMethodID(cls, method, sig);
    if (env->ExceptionCheck()) {
        return;
    }
    va_list args;
    va_start(args, obj);
    env->CallNonvirtualObjectMethodV(obj, cls, methodID, args);
    va_end(args);
    if (env->ExceptionCheck()) {
        return;
    }
}

#define JNI_CALL_NONVIRT_TYPE_METHOD(type, name) \
type JNICallNonvirtual##name##Method(JNIEnv *env, const char* clazz, const char *method, const char* sig, jobject obj, ...) { \
    jclass cls = env->FindClass(clazz); \
    if (env->ExceptionCheck()) { \
        return (type) 0; \
    } \
    jmethodID methodID = env->GetMethodID(cls, method, sig); \
    if (env->ExceptionCheck()) { \
        return (type) 0; \
    } \
    va_list args; \
    va_start(args, obj); \
    type result = env->CallNonvirtual##name##MethodV(obj, cls, methodID, args); \
    va_end(args); \
    if (env->ExceptionCheck()) { \
        return (type) 0; \
    } \
    return result; \
}

JNI_CALL_NONVIRT_TYPE_METHOD(jobject, Object)
JNI_CALL_NONVIRT_TYPE_METHOD(jboolean, Boolean)
JNI_CALL_NONVIRT_TYPE_METHOD(jbyte, Byte)
JNI_CALL_NONVIRT_TYPE_METHOD(jchar, Char)
JNI_CALL_NONVIRT_TYPE_METHOD(jshort, Short)
JNI_CALL_NONVIRT_TYPE_METHOD(jint, Int)
JNI_CALL_NONVIRT_TYPE_METHOD(jlong, Long)
JNI_CALL_NONVIRT_TYPE_METHOD(jfloat, Float)
JNI_CALL_NONVIRT_TYPE_METHOD(jdouble, Double)

void JNICallStaticVoidMethod(JNIEnv *env, const char* clazz, const char *method, const char* sig, ...) {
    jclass cls = env->FindClass(clazz);
    if (env->ExceptionCheck()) {
        return;
    }
    jmethodID methodID = env->GetStaticMethodID(cls, method, sig);
    if (env->ExceptionCheck()) {
        return;
    }
    va_list args;
    va_start(args, sig);
    env->CallStaticVoidMethod(cls, methodID, args);
    va_end(args);
    if (env->ExceptionCheck()) {
        return;
    }
}

#define JNI_CALL_STATIC_TYPE_METHOD(type, name) \
type JNICallStatic##name##Method(JNIEnv *env, const char* clazz, const char *method, const char* sig, ...) { \
    jclass cls = env->FindClass(clazz); \
    if (env->ExceptionCheck()) { \
        return (type) 0; \
    } \
    jmethodID methodID = env->GetStaticMethodID(cls, method, sig); \
    if (env->ExceptionCheck()) { \
        return (type) 0; \
    } \
    va_list args; \
    va_start(args, sig); \
    type result = env->CallStatic##name##Method(cls, methodID, args); \
    va_end(args); \
    if (env->ExceptionCheck()) { \
        return (type) 0; \
    } \
    return result; \
}

JNI_CALL_STATIC_TYPE_METHOD(jobject, Object)
JNI_CALL_STATIC_TYPE_METHOD(jboolean, Boolean)
JNI_CALL_STATIC_TYPE_METHOD(jbyte, Byte)
JNI_CALL_STATIC_TYPE_METHOD(jchar, Char)
JNI_CALL_STATIC_TYPE_METHOD(jshort, Short)
JNI_CALL_STATIC_TYPE_METHOD(jint, Int)
JNI_CALL_STATIC_TYPE_METHOD(jlong, Long)
JNI_CALL_STATIC_TYPE_METHOD(jfloat, Float)
JNI_CALL_STATIC_TYPE_METHOD(jdouble, Double)

#define JNI_GET_TYPE_FIELD(type, name) \
type JNIGet##name##Field(JNIEnv* env, const char* clazz, const char* field, const char* sig, jobject obj) { \
    jclass cls = env->FindClass(clazz); \
    if (env->ExceptionCheck()) { \
        return (type) 0; \
    } \
    jfieldID fieldID = env->GetFieldID(cls, field, sig); \
    if (env->ExceptionCheck()) { \
        return (type) 0; \
    } \
    type result = env->Get##name##Field(obj, fieldID); \
    if (env->ExceptionCheck()) { \
        return (type) 0; \
    } \
    return result; \
}

JNI_GET_TYPE_FIELD(jobject, Object)
JNI_GET_TYPE_FIELD(jboolean, Boolean)
JNI_GET_TYPE_FIELD(jbyte, Byte)
JNI_GET_TYPE_FIELD(jchar, Char)
JNI_GET_TYPE_FIELD(jshort, Short)
JNI_GET_TYPE_FIELD(jint, Int)
JNI_GET_TYPE_FIELD(jlong, Long)
JNI_GET_TYPE_FIELD(jfloat, Float)
JNI_GET_TYPE_FIELD(jdouble, Double)

#define JNI_GET_STATIC_TYPE_FIELD(type, name) \
type JNIGetStatic##name##Field(JNIEnv* env, const char* clazz, const char* field, const char* sig) { \
    jclass cls = env->FindClass(clazz); \
    if (env->ExceptionCheck()) { \
        return (type) 0; \
    } \
    jfieldID fieldID = env->GetStaticFieldID(cls, field, sig); \
    if (env->ExceptionCheck()) { \
        return (type) 0; \
    } \
    type result = env->GetStatic##name##Field(cls, fieldID); \
    if (env->ExceptionCheck()) { \
        return (type) 0; \
    } \
    return result; \
}

JNI_GET_STATIC_TYPE_FIELD(jobject, Object)
JNI_GET_STATIC_TYPE_FIELD(jboolean, Boolean)
JNI_GET_STATIC_TYPE_FIELD(jbyte, Byte)
JNI_GET_STATIC_TYPE_FIELD(jchar, Char)
JNI_GET_STATIC_TYPE_FIELD(jshort, Short)
JNI_GET_STATIC_TYPE_FIELD(jint, Int)
JNI_GET_STATIC_TYPE_FIELD(jlong, Long)
JNI_GET_STATIC_TYPE_FIELD(jfloat, Float)
JNI_GET_STATIC_TYPE_FIELD(jdouble, Double)

#define JNI_SET_TYPE_FIELD(type, name) \
void JNISet##name##Field(JNIEnv* env, const char* clazz, const char* field, const char* sig, jobject obj, type value) { \
    jclass cls = env->FindClass(clazz); \
    if (env->ExceptionCheck()) { \
        return; \
    } \
    jfieldID fieldID = env->GetFieldID(cls, field, sig); \
    if (env->ExceptionCheck()) { \
        return; \
    } \
    env->Set##name##Field(obj, fieldID, value); \
    if (env->ExceptionCheck()) { \
        return; \
    } \
}

JNI_SET_TYPE_FIELD(jobject, Object)
JNI_SET_TYPE_FIELD(jboolean, Boolean)
JNI_SET_TYPE_FIELD(jbyte, Byte)
JNI_SET_TYPE_FIELD(jchar, Char)
JNI_SET_TYPE_FIELD(jshort, Short)
JNI_SET_TYPE_FIELD(jint, Int)
JNI_SET_TYPE_FIELD(jlong, Long)
JNI_SET_TYPE_FIELD(jfloat, Float)
JNI_SET_TYPE_FIELD(jdouble, Double)

#define JNI_SET_STATIC_TYPE_FIELD(type, name) \
void JNISetStatic##name##Field(JNIEnv* env, const char* clazz, const char* field, const char* sig, type value) { \
    jclass cls = env->FindClass(clazz); \
    if (env->ExceptionCheck()) { \
        return; \
    } \
    jfieldID fieldID = env->GetStaticFieldID(cls, field, sig); \
    if (env->ExceptionCheck()) { \
        return; \
    } \
    env->SetStatic##name##Field(cls, fieldID, value); \
    if (env->ExceptionCheck()) { \
        return; \
    } \
}

JNI_SET_STATIC_TYPE_FIELD(jobject, Object)
JNI_SET_STATIC_TYPE_FIELD(jboolean, Boolean)
JNI_SET_STATIC_TYPE_FIELD(jbyte, Byte)
JNI_SET_STATIC_TYPE_FIELD(jchar, Char)
JNI_SET_STATIC_TYPE_FIELD(jshort, Short)
JNI_SET_STATIC_TYPE_FIELD(jint, Int)
JNI_SET_STATIC_TYPE_FIELD(jlong, Long)
JNI_SET_STATIC_TYPE_FIELD(jfloat, Float)
JNI_SET_STATIC_TYPE_FIELD(jdouble, Double)
