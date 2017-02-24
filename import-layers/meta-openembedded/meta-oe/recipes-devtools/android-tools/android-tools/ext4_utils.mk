# Makefile for ext4_utils

SRCDIR ?= $(S)

VPATH += $(SRCDIR)/system/extras/ext4_utils
make_ext4fs_SRC_FILES += make_ext4fs_main.c
make_ext4fs_SRC_FILES += canned_fs_config.c
make_ext4fs_OBJS := $(make_ext4fs_SRC_FILES:.c=.o)

ext2simg_SRC_FILES += ext2simg.c
ext2simg_OBJS := $(ext2simg_SRC_FILES:.c=.o)

ext4fixup_SRC_FILES += ext4fixup_main.c
ext4fixup_OBJS := $(ext4fixup_SRC_FILES:.c=.o)

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
simg2img_SRC_FILES += simg2img.c
simg2img_SRC_FILES += sparse_crc32.c
simg2img_OBJS := $(simg2img_SRC_FILES:.c=.o)

img2simg_SRC_FILES += img2simg.c
img2simg_OBJS := $(img2simg_SRC_FILES:.c=.o)

simg2simg_SRC_FILES += simg2simg.c
simg2simg_SRC_FILES += sparse_crc32.c
simg2simg_OBJS := $(simg2simg_SRC_FILES:.c=.o)

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

CFLAGS += -DANDROID
CFLAGS += -DHOST
CFLAGS += -I$(SRCDIR)/system/extras/ext4_utils
CFLAGS += -I$(SRCDIR)/system/core/include
CFLAGS += -I$(SRCDIR)/system/core/libsparse/include
CFLAGS += -I$(SRCDIR)/external/libselinux/include
CFLAGS += -include $(SRCDIR)/build/core/combo/include/arch/$(android_arch)/AndroidConfig.h

all: make_ext4fs ext2simg ext4fixup simg2img img2simg simg2simg

make_ext4fs: libext4_utils.a libsparse.a libselinux.a $(make_ext4fs_OBJS)
	$(CC) -o $@ $(LDFLAGS) $(make_ext4fs_OBJS) \
		libext4_utils.a libsparse.a libselinux.a -lz -lpcre

ext2simg: libext4_utils.a libselinux.a libsparse.a $(ext2simg_OBJS)
	$(CC) -o $@ $(LDFLAGS) $(ext2simg_OBJS) \
		libext4_utils.a libselinux.a libsparse.a -lz -lpcre

ext4fixup: libext4_utils.a libsparse.a $(ext4fixup_OBJS)
	$(CC) -o $@ $(LDFLAGS) $(ext4fixup_OBJS) libext4_utils.a libsparse.a -lz

simg2img: libsparse.a $(simg2img_OBJS)
	$(CC) -o $@ $(LDFLAGS) $(simg2img_OBJS) libsparse.a -lz

img2simg: libsparse.a $(img2simg_OBJS)
	$(CC) -o $@ $(LDFLAGS) $(img2simg_OBJS) libsparse.a -lz

simg2simg: libsparse.a $(simg2simg_OBJS)
	$(CC) -o $@ $(LDFLAGS) $(simg2simg_OBJS) libsparse.a -lz

libext4_utils.a: $(libext4_utils_OBJS)
	$(AR) rcs $@ $(libext4_utils_OBJS)

libsparse.a: $(libsparse_OBJS)
	$(AR) rcs $@ $(libsparse_OBJS)

libselinux.a: $(libselinux_OBJS)
	$(AR) rcs $@ $(libselinux_OBJS)

clean:
	$(RM) $(make_ext4fs_OBJS) $(ext2simg_OBJS) $(ext4fixup_OBJS) \
		$(simg2img_OBJS) $(img2simg_OBJS) $(simg2simg_OBJS) \
		$(libext4_utils_OBJS) $(libsparse_OBJS) $(libselinux_OBJS) \
		make_ext4fs ext2simg ext4fixup simg2img img2simg simg2simg *.a
