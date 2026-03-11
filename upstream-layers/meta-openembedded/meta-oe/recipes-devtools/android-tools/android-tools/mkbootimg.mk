# Makefile for mkbootimg

SRCDIR ?= $(S)

VPATH += $(SRCDIR)/system/core/mkbootimg
mkbootimg_SRC_FILES += mkbootimg.c
mkbootimg_OBJS := $(mkbootimg_SRC_FILES:.c=.o)

VPATH += $(SRCDIR)/system/core/libmincrypt
libmincrypt_SRC_FILES := dsa_sig.c p256.c p256_ec.c p256_ecdsa.c rsa.c sha.c sha256.c
libmincrypt_OBJS := $(libmincrypt_SRC_FILES:.c=.o)

CFLAGS += -DANDROID
CFLAGS += -I$(SRCDIR)/system/core/mkbootimg
CFLAGS += -I$(SRCDIR)/system/core/include
CFLAGS += -include $(SRCDIR)/build/core/combo/include/arch/$(android_arch)/AndroidConfig.h

LIBS += libmincrypt.a

all: mkbootimg

mkbootimg: libmincrypt.a $(mkbootimg_OBJS)
	$(CC) -o $@ $(LDFLAGS) $(mkbootimg_OBJS) $(LIBS)

libmincrypt.a: $(libmincrypt_OBJS)
	$(AR) rcs $@ $(libmincrypt_OBJS)

clean:
	$(RM) $(mkbootimg_OBJS) $(libmincrypt_OBJS) mkbootimg *.a
