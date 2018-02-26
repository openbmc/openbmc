# Makefile for adbd

SRCDIR ?= $(S)

VPATH += $(SRCDIR)/system/core/adb
adbd_SRC_FILES += adb.c
adbd_SRC_FILES += fdevent.c
adbd_SRC_FILES += transport.c
adbd_SRC_FILES += transport_local.c
adbd_SRC_FILES += transport_usb.c
adbd_SRC_FILES += adb_auth_client.c
adbd_SRC_FILES += sockets.c
adbd_SRC_FILES += services.c
adbd_SRC_FILES += file_sync_service.c
adbd_SRC_FILES += jdwp_service.c
adbd_SRC_FILES += framebuffer_service.c
adbd_SRC_FILES += remount_service.c
adbd_SRC_FILES += disable_verity_service.c
adbd_SRC_FILES += base64.c
adbd_SRC_FILES += usb_linux_client.c
adbd_OBJS := $(adbd_SRC_FILES:.c=.o)

VPATH += $(SRCDIR)/system/core/liblog
liblog_SRC_FILES += logd_write.c
liblog_SRC_FILES += log_event_write.c
liblog_SRC_FILES += logprint.c
liblog_SRC_FILES += event_tag_map.c
liblog_SRC_FILES += fake_log_device.c
liblog_OBJS := $(liblog_SRC_FILES:.c=.o)

VPATH += $(SRCDIR)/system/core/fs_mgr
fs_mgr_SRC_FILES += fs_mgr_fstab.c
fs_mgr_OBJS := $(fs_mgr_SRC_FILES:.c=.o)

VPATH += $(SRCDIR)/system/core/libcutils
libcutils_SRC_FILES += atomic.c
libcutils_SRC_FILES += hashmap.c
libcutils_SRC_FILES += native_handle.c
libcutils_SRC_FILES += config_utils.c
libcutils_SRC_FILES += cpu_info.c
libcutils_SRC_FILES += load_file.c
# libcutils_SRC_FILES += open_memstream.c
# libcutils_SRC_FILES += strdup16to8.c
# libcutils_SRC_FILES += strdup8to16.c
# libcutils_SRC_FILES += record_stream.c
# libcutils_SRC_FILES += process_name.c
# libcutils_SRC_FILES += threads.c
# libcutils_SRC_FILES += sched_policy.c
# libcutils_SRC_FILES += iosched_policy.c
libcutils_SRC_FILES += str_parms.c
libcutils_SRC_FILES += fs.c
libcutils_SRC_FILES += multiuser.c
libcutils_SRC_FILES += socket_inaddr_any_server.c
libcutils_SRC_FILES += socket_local_client.c
libcutils_SRC_FILES += socket_local_server.c
libcutils_SRC_FILES += socket_loopback_client.c
libcutils_SRC_FILES += socket_loopback_server.c
libcutils_SRC_FILES += socket_network_client.c
libcutils_SRC_FILES += sockets.c
libcutils_SRC_FILES += ashmem-host.c
libcutils_SRC_FILES += dlmalloc_stubs.c
libcutils_SRC_FILES += klog.c
libcutils_SRC_FILES += properties.c
libcutils_OBJS := $(libcutils_SRC_FILES:.c=.o)

VPATH += $(SRCDIR)/external/libselinux/src
libselinux_SRC_FILES += booleans.c
libselinux_SRC_FILES += canonicalize_context.c
libselinux_SRC_FILES += disable.c
libselinux_SRC_FILES += enabled.c
libselinux_SRC_FILES += fgetfilecon.c
libselinux_SRC_FILES += fsetfilecon.c
libselinux_SRC_FILES += getenforce.c
libselinux_SRC_FILES += getfilecon.c
libselinux_SRC_FILES += getpeercon.c
libselinux_SRC_FILES += lgetfilecon.c
libselinux_SRC_FILES += load_policy.c
libselinux_SRC_FILES += lsetfilecon.c
libselinux_SRC_FILES += policyvers.c
libselinux_SRC_FILES += procattr.c
libselinux_SRC_FILES += setenforce.c
libselinux_SRC_FILES += setfilecon.c
libselinux_SRC_FILES += context.c
libselinux_SRC_FILES += mapping.c
libselinux_SRC_FILES += stringrep.c
libselinux_SRC_FILES += compute_create.c
libselinux_SRC_FILES += compute_av.c
libselinux_SRC_FILES += avc.c
libselinux_SRC_FILES += avc_internal.c
libselinux_SRC_FILES += avc_sidtab.c
libselinux_SRC_FILES += get_initial_context.c
libselinux_SRC_FILES += checkAccess.c
libselinux_SRC_FILES += sestatus.c
libselinux_SRC_FILES += deny_unknown.c

libselinux_SRC_FILES += callbacks.c
libselinux_SRC_FILES += check_context.c
libselinux_SRC_FILES += freecon.c
libselinux_SRC_FILES += init.c
libselinux_SRC_FILES += label.c
libselinux_SRC_FILES += label_file.c
libselinux_SRC_FILES += label_android_property.c
libselinux_OBJS := $(libselinux_SRC_FILES:.c=.o)

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

CFLAGS += -std=gnu11
CFLAGS += -DANDROID
CFLAGS += -DADB_HOST=0
CFLAGS += -D_XOPEN_SOURCE -D_GNU_SOURCE
CFLAGS += -DALLOW_ADBD_ROOT=1
CFLAGS += -DALLOW_ADBD_DISABLE_VERITY=1
CFLAGS += -DPROP_NAME_MAX=32
CFLAGS += -DPROP_VALUE_MAX=92
CFLAGS += -DAUDITD_LOG_TAG=1003
# CFLAGS += -DHOST
CFLAGS += -DANDROID_SMP=0
CFLAGS += -I$(SRCDIR)/system/core/adb
CFLAGS += -I$(SRCDIR)/system/core/include
CFLAGS += -I$(SRCDIR)/system/core/libsparse/include
CFLAGS += -I$(SRCDIR)/system/extras/ext4_utils
CFLAGS += -I$(SRCDIR)/system/core/fs_mgr/include
CFLAGS += -I$(SRCDIR)/hardware/libhardware/include
CFLAGS += -I$(SRCDIR)/external/libselinux/include
CFLAGS += -include $(SRCDIR)/build/core/combo/include/arch/$(android_arch)/AndroidConfig.h

LIBS += liblog.a libfs_mgr.a libcutils.a libselinux.a libext4_utils.a -lpthread -lbsd -lpcre -lresolv -lcrypto

all: adbd

adbd: liblog.a libfs_mgr.a libcutils.a libselinux.a libext4_utils.a $(adbd_OBJS)
	$(CC) -o $@ $(LDFLAGS) $(adbd_OBJS) $(LIBS)

liblog.a: $(liblog_OBJS)
	$(AR) rcs $@ $(liblog_OBJS)

libfs_mgr.a: $(fs_mgr_OBJS)
	$(AR) rcs $@ $(fs_mgr_OBJS)

libcutils.a: $(libcutils_OBJS)
	$(AR) rcs $@ $(libcutils_OBJS)

libselinux.a: $(libselinux_OBJS)
	export CFLAGS="-DANDROID -DHOST"
	$(AR) rcs $@ $(libselinux_OBJS)

libext4_utils.a: $(libext4_utils_OBJS)
	$(AR) rcs $@ $(libext4_utils_OBJS)

clean:
	$(RM) *.o *.a adbd
