# Makefile for fastboot

SRCDIR ?= $(S)

VPATH += $(SRCDIR)/system/core/fastboot
fastboot_SRC_FILES += protocol.c
fastboot_SRC_FILES += engine.c
fastboot_SRC_FILES += bootimg.c
fastboot_SRC_FILES += fastboot.c
fastboot_SRC_FILES += util.c
fastboot_SRC_FILES += fs.c
fastboot_SRC_FILES += usb_linux.c
fastboot_SRC_FILES += util_linux.c
fastboot_OBJS := $(fastboot_SRC_FILES:.c=.o)

VPATH += $(SRCDIR)/system/core/libzipfile
libzipfile_SRC_FILES += centraldir.c
libzipfile_SRC_FILES += zipfile.c
libzipfile_OBJS := $(libzipfile_SRC_FILES:.c=.o)

VPATH += $(SRCDIR)/system/extras/ext4_utils
libext4_utils_SRC_FILES += make_ext4fs.c
libext4_utils_SRC_FILES += ext4fixup.c
libext4_utils_SRC_FILES += ext4_utils.c
libext4_utils_SRC_FILES += allocate.c
libext4_utils_SRC_FILES += contents.c
libext4_utils_SRC_FILES += extent.c
libext4_utils_SRC_FILES += indirect.c
libext4_utils_SRC_FILES += uuid.c
libext4_utils_SRC_FILES += sha1.c
libext4_utils_SRC_FILES += wipe.c
libext4_utils_SRC_FILES += crc16.c
libext4_utils_SRC_FILES += ext4_sb.c
libext4_utils_OBJS := $(libext4_utils_SRC_FILES:.c=.o)

VPATH += $(SRCDIR)/system/core/libsparse
libsparse_SRC_FILES += backed_block.c
libsparse_SRC_FILES += output_file.c
libsparse_SRC_FILES += sparse.c
libsparse_SRC_FILES += sparse_crc32.c
libsparse_SRC_FILES += sparse_err.c
libsparse_SRC_FILES += sparse_read.c
libsparse_OBJS := $(libsparse_SRC_FILES:.c=.o)

VPATH += $(SRCDIR)/external/libselinux/src
libselinux_SRC_FILES += callbacks.c
libselinux_SRC_FILES += check_context.c
libselinux_SRC_FILES += freecon.c
libselinux_SRC_FILES += init.c
libselinux_SRC_FILES += label.c
libselinux_SRC_FILES += label_file.c
libselinux_SRC_FILES += label_android_property.c
libselinux_OBJS := $(libselinux_SRC_FILES:.c=.o)

CFLAGS += -std=gnu11
CFLAGS += -DANDROID
# CFLAGS += -DUSE_F2FS
CFLAGS += -DHOST
CFLAGS += -I$(SRCDIR)/system/core/fastboot
CFLAGS += -I$(SRCDIR)/system/core/include
CFLAGS += -I$(SRCDIR)/system/core/mkbootimg
CFLAGS += -I$(SRCDIR)/system/extras/ext4_utils
CFLAGS += -I$(SRCDIR)/system/extras/f2fs_utils
CFLAGS += -I$(SRCDIR)/system/core/libsparse/include
CFLAGS += -I$(SRCDIR)/external/libselinux/include
CFLAGS += -include $(SRCDIR)/build/core/combo/include/arch/$(android_arch)/AndroidConfig.h

LIBS += libzipfile.a libext4_utils.a libsparse.a libselinux.a -lz -lpcre

all: fastboot

fastboot: libzipfile.a libext4_utils.a libsparse.a libselinux.a $(fastboot_OBJS)
	$(CC) -o $@ $(LDFLAGS) $(fastboot_OBJS) $(LIBS)

libzipfile.a: $(libzipfile_OBJS)
	$(AR) rcs $@ $(libzipfile_OBJS)

libext4_utils.a: $(libext4_utils_OBJS)
	$(AR) rcs $@ $(libext4_utils_OBJS)

libsparse.a: $(libsparse_OBJS)
	$(AR) rcs $@ $(libsparse_OBJS)

libselinux.a: $(libselinux_OBJS)
	$(AR) rcs $@ $(libselinux_OBJS)

clean:
	$(RM) $(fastboot_OBJS) $(libzipfile_OBJS) $(libext4_utils_OBJS) \
		$(libsparse_OBJS) $(libselinux_OBJS) fastboot *.a
