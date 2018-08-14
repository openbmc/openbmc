# Makefile for adb

SRCDIR ?= $(S)

VPATH += $(SRCDIR)/system/core/adb
adb_SRC_FILES += adb.c
adb_SRC_FILES += console.c
adb_SRC_FILES += transport.c
adb_SRC_FILES += transport_local.c
adb_SRC_FILES += transport_usb.c
adb_SRC_FILES += commandline.c
adb_SRC_FILES += adb_client.c
adb_SRC_FILES += adb_auth_host.c
adb_SRC_FILES += sockets.c
adb_SRC_FILES += services.c
adb_SRC_FILES += file_sync_client.c
adb_SRC_FILES += get_my_path_linux.c
adb_SRC_FILES += usb_linux.c
adb_SRC_FILES += usb_vendors.c
adb_SRC_FILES += fdevent.c
adb_OBJS := $(adb_SRC_FILES:.c=.o)

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
libcutils_OBJS := $(libcutils_SRC_FILES:.c=.o)

CFLAGS += -DANDROID
CFLAGS += -DWORKAROUND_BUG6558362
CFLAGS += -DADB_HOST=1
CFLAGS += -D_XOPEN_SOURCE -D_GNU_SOURCE
CFLAGS += -DANDROID_SMP=0
CFLAGS += -I$(SRCDIR)/system/core/adb
CFLAGS += -I$(SRCDIR)/system/core/include
CFLAGS += -include $(SRCDIR)/build/core/combo/include/arch/$(android_arch)/AndroidConfig.h

LIBS += libcutils.a -lpthread -lcrypto

all: adb

adb: libcutils.a $(adb_OBJS)
	$(CC) -o $@ $(LDFLAGS) $(adb_OBJS) $(LIBS)

libcutils.a: $(libcutils_OBJS)
	$(AR) rcs $@ $(libcutils_OBJS)

clean:
	$(RM) $(adb_OBJS) $(libcutils_OBJS) adb *.a
