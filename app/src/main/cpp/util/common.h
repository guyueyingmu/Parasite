#ifndef _COMMON_H_HUJI
#define _COMMON_H_HUJI

#define DEBUG

#ifndef IN
#define IN
#endif /* IN */

#ifndef OUT
#define OUT
#endif /* OUT */

#ifndef NULL
#ifdef __cplusplus
#define NULL 0
#else /* __cplusplus */
#define NULL ((void *)0)
#endif /* __cplusplus */
#endif /* NULL */

#define JNIHIDDEN __attribute__ ((visibility ("hidden")))

#define CONSTRUCTOR __attribute__((constructor))
#define DESTRUCTOR __attribute__((destructor))

#define COUNT_OF(ary) (sizeof((ary)) / sizeof((ary)[0]))
#define MEMBER_OF(object, offset) (((size_t)object) + offset)
#define OFFSET_OF(struct, member) ((size_t)&(((struct*)NULL)->member))

// align必须为2的n次幂
#define ALIGN_UP(value, align) ((((size_t)value) + (align) - 1) & (~((align) - 1)))
#define ALIGN_DOWN(value, align) (((size_t)value) & (~((align) - 1)))

#ifdef DEBUG
#define TRACE(format, ...) do { printf(format, ##__VA_ARGS__); } while (false)
#else /* DEBUG */
#define TRACE(format, ...)
#endif /* DEBUG */

#include <android/log.h>

#ifdef DEBUG
#define LOGD(tag, ...) __android_log_print(ANDROID_LOG_DEBUG, tag, __VA_ARGS__)
#define LOGI(tag, ...) __android_log_print(ANDROID_LOG_INFO, tag, __VA_ARGS__)
#define LOGE(tag, ...) __android_log_print(ANDROID_LOG_ERROR, tag, __VA_ARGS__)
#else /* DEBUG */
#define LOGD(tag, ...)
#define LOGI(tag, ...)
#define LOGE(tag, ...)
#endif /* DEBUG */

#ifdef __cplusplus
#define CAPTURE_EXCEPTION(env) ((env)->ExceptionCheck() ? \
    ((env)->ExceptionDescribe(), (env)->ExceptionClear(), true) : false)
#else /* __cplusplus */
#define CAPTURE_EXCEPTION(env) ((*env)->ExceptionCheck(env) ? \
    ((*env)->ExceptionDescribe(env), (*env)->ExceptionClear(env), true) : false)
#endif /* __cplusplus */

#define CAPTURE_EXCEPTION_RETURN(env, ret) { if (CAPTURE_EXCEPTION(env)) return ret; }
#define CAPTURE_EXCEPTION_RETURN_VOID(env) { if (CAPTURE_EXCEPTION(env)) return; }

#endif /* _COMMON_H_HUJI */
