#ifndef _JNIUTIL_H_HUJI
#define _JNIUTIL_H_HUJI

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

int JNIRegisterNatives(JNIEnv* env, const char* clazz, JNINativeMethod* methods, jint nMethods);

jmethodID JNIGetMethodID(JNIEnv *env, const char* clazz, const char *method, const char* sig);
jmethodID JNIGetStaticMethodID(JNIEnv *env, const char* clazz, const char *method, const char* sig);

jfieldID JNIGetFieldID(JNIEnv* env, const char* clazz, const char* field, const char* sig);
jfieldID JNIGetStaticFieldID(JNIEnv* env, const char* clazz, const char* field, const char* sig);

jobject JNINewObject(JNIEnv *env, const char* clazz, const char* sig, ...);
jobject JNINewObjectArray(JNIEnv *env, const char* clazz, jsize length, jobject initial);

#define JNI_DECLARE_CALL_TYPE_METHOD(type, name) \
type JNICall##name##Method(JNIEnv *env, const char* clazz, const char *method, const char* sig, jobject obj, ...);

JNI_DECLARE_CALL_TYPE_METHOD(void, Void)
JNI_DECLARE_CALL_TYPE_METHOD(jobject, Object)
JNI_DECLARE_CALL_TYPE_METHOD(jboolean, Boolean)
JNI_DECLARE_CALL_TYPE_METHOD(jbyte, Byte)
JNI_DECLARE_CALL_TYPE_METHOD(jchar, Char)
JNI_DECLARE_CALL_TYPE_METHOD(jshort, Short)
JNI_DECLARE_CALL_TYPE_METHOD(jint, Int)
JNI_DECLARE_CALL_TYPE_METHOD(jlong, Long)
JNI_DECLARE_CALL_TYPE_METHOD(jfloat, Float)
JNI_DECLARE_CALL_TYPE_METHOD(jdouble, Double)

#define JNI_DECLARE_CALL_NONVIRT_TYPE_METHOD(type, name) \
type JNICallNonvirtual##name##Method(JNIEnv *env, const char* clazz, const char *method, const char* sig, jobject obj, ...);

JNI_DECLARE_CALL_NONVIRT_TYPE_METHOD(void, Void)
JNI_DECLARE_CALL_NONVIRT_TYPE_METHOD(jobject, Object)
JNI_DECLARE_CALL_NONVIRT_TYPE_METHOD(jboolean, Boolean)
JNI_DECLARE_CALL_NONVIRT_TYPE_METHOD(jbyte, Byte)
JNI_DECLARE_CALL_NONVIRT_TYPE_METHOD(jchar, Char)
JNI_DECLARE_CALL_NONVIRT_TYPE_METHOD(jshort, Short)
JNI_DECLARE_CALL_NONVIRT_TYPE_METHOD(jint, Int)
JNI_DECLARE_CALL_NONVIRT_TYPE_METHOD(jlong, Long)
JNI_DECLARE_CALL_NONVIRT_TYPE_METHOD(jfloat, Float)
JNI_DECLARE_CALL_NONVIRT_TYPE_METHOD(jdouble, Double)

#define JNI_DECLARE_CALL_STATIC_TYPE_METHOD(type, name) \
type JNICallStatic##name##Method(JNIEnv *env, const char* clazz, const char *method, const char* sig, ...);

JNI_DECLARE_CALL_STATIC_TYPE_METHOD(void, Void)
JNI_DECLARE_CALL_STATIC_TYPE_METHOD(jobject, Object)
JNI_DECLARE_CALL_STATIC_TYPE_METHOD(jboolean, Boolean)
JNI_DECLARE_CALL_STATIC_TYPE_METHOD(jbyte, Byte)
JNI_DECLARE_CALL_STATIC_TYPE_METHOD(jchar, Char)
JNI_DECLARE_CALL_STATIC_TYPE_METHOD(jshort, Short)
JNI_DECLARE_CALL_STATIC_TYPE_METHOD(jint, Int)
JNI_DECLARE_CALL_STATIC_TYPE_METHOD(jlong, Long)
JNI_DECLARE_CALL_STATIC_TYPE_METHOD(jfloat, Float)
JNI_DECLARE_CALL_STATIC_TYPE_METHOD(jdouble, Double)

#define JNI_DECLARE_GET_TYPE_FIELD(type, name) \
type JNIGet##name##Field(JNIEnv* env, const char* clazz, const char* field, const char* sig, jobject obj);

JNI_DECLARE_GET_TYPE_FIELD(jobject, Object)
JNI_DECLARE_GET_TYPE_FIELD(jboolean, Boolean)
JNI_DECLARE_GET_TYPE_FIELD(jbyte, Byte)
JNI_DECLARE_GET_TYPE_FIELD(jchar, Char)
JNI_DECLARE_GET_TYPE_FIELD(jshort, Short)
JNI_DECLARE_GET_TYPE_FIELD(jint, Int)
JNI_DECLARE_GET_TYPE_FIELD(jlong, Long)
JNI_DECLARE_GET_TYPE_FIELD(jfloat, Float)
JNI_DECLARE_GET_TYPE_FIELD(jdouble, Double)

#define JNI_DECLARE_GET_STATIC_TYPE_FIELD(type, name) \
type JNIGetStatic##name##Field(JNIEnv* env, const char* clazz, const char* field, const char* sig);

JNI_DECLARE_GET_STATIC_TYPE_FIELD(jobject, Object)
JNI_DECLARE_GET_STATIC_TYPE_FIELD(jboolean, Boolean)
JNI_DECLARE_GET_STATIC_TYPE_FIELD(jbyte, Byte)
JNI_DECLARE_GET_STATIC_TYPE_FIELD(jchar, Char)
JNI_DECLARE_GET_STATIC_TYPE_FIELD(jshort, Short)
JNI_DECLARE_GET_STATIC_TYPE_FIELD(jint, Int)
JNI_DECLARE_GET_STATIC_TYPE_FIELD(jlong, Long)
JNI_DECLARE_GET_STATIC_TYPE_FIELD(jfloat, Float)
JNI_DECLARE_GET_STATIC_TYPE_FIELD(jdouble, Double)

#define JNI_DECLARE_SET_TYPE_FIELD(type, name) \
void JNISet##name##Field(JNIEnv* env, const char* clazz, const char* field, const char* sig, jobject obj, type value);

JNI_DECLARE_SET_TYPE_FIELD(jobject, Object)
JNI_DECLARE_SET_TYPE_FIELD(jboolean, Boolean)
JNI_DECLARE_SET_TYPE_FIELD(jbyte, Byte)
JNI_DECLARE_SET_TYPE_FIELD(jchar, Char)
JNI_DECLARE_SET_TYPE_FIELD(jshort, Short)
JNI_DECLARE_SET_TYPE_FIELD(jint, Int)
JNI_DECLARE_SET_TYPE_FIELD(jlong, Long)
JNI_DECLARE_SET_TYPE_FIELD(jfloat, Float)
JNI_DECLARE_SET_TYPE_FIELD(jdouble, Double)

#define JNI_DECLARE_SET_STATIC_TYPE_FIELD(type, name) \
void JNISetStatic##name##Field(JNIEnv* env, const char* clazz, const char* field, const char* sig, type value);

JNI_DECLARE_SET_STATIC_TYPE_FIELD(jobject, Object)
JNI_DECLARE_SET_STATIC_TYPE_FIELD(jboolean, Boolean)
JNI_DECLARE_SET_STATIC_TYPE_FIELD(jbyte, Byte)
JNI_DECLARE_SET_STATIC_TYPE_FIELD(jchar, Char)
JNI_DECLARE_SET_STATIC_TYPE_FIELD(jshort, Short)
JNI_DECLARE_SET_STATIC_TYPE_FIELD(jint, Int)
JNI_DECLARE_SET_STATIC_TYPE_FIELD(jlong, Long)
JNI_DECLARE_SET_STATIC_TYPE_FIELD(jfloat, Float)
JNI_DECLARE_SET_STATIC_TYPE_FIELD(jdouble, Double)

#ifdef __cplusplus
}
#endif

#endif /* _JNIUTIL_H_HUJI */
