include rules_yocto.mk
NAME = adbd

SOURCES = \
	adb/adbconnection/adbconnection_server.cpp \
	adb/daemon/auth.cpp \
	adb/daemon/file_sync_service.cpp \
	adb/daemon/file_sync_service.h \
	adb/daemon/framebuffer_service.cpp \
	adb/daemon/framebuffer_service.h \
	adb/daemon/jdwp_service.cpp \
	adb/daemon/main.cpp \
	adb/daemon/restart_service.cpp \
	adb/daemon/restart_service.h \
	adb/daemon/services.cpp \
	adb/daemon/shell_service.cpp \
	adb/daemon/shell_service.h \
	adb/daemon/usb_ffs.cpp \
	adb/daemon/usb_legacy.cpp \
	adb/daemon/usb.cpp \
	adb/shell_service_protocol.cpp \
	adb/adb.cpp \
	adb/adb_io.cpp \
	adb/adb_listeners.cpp \
	adb/adb_trace.cpp \
	adb/adb_unique_fd.cpp \
	adb/adb_utils.cpp \
	adb/fdevent/fdevent.cpp \
	adb/fdevent/fdevent_epoll.cpp \
	adb/services.cpp \
	adb/sockets.cpp \
	adb/socket_spec.cpp \
	adb/sysdeps/errno.cpp \
	adb/sysdeps/posix/network.cpp \
	adb/sysdeps_unix.cpp \
	adb/transport.cpp \
	adb/transport_fd.cpp \
	adb/transport_local.cpp \
	adb/transport_usb.cpp \
	adb/types.cpp \
	diagnose_usb/diagnose_usb.cpp \
	libasyncio/AsyncIO.cpp \

SOURCES := $(foreach source, $(SOURCES), system/core/$(source))

SOURCES += \
    frameworks/native/libs/adbd_auth/adbd_auth.cpp

CXXFLAGS += -std=gnu++20
CPPFLAGS += -Isystem/coreinclude -Isystem/core/adb -Isystem/core/base/include  -Idebian/out/system/core -Isystem/tools/mkbootimg/include/bootimg -Isystem/core/fs_mgr/include \
	    -Isystem/core/fs_mgr/include_fstab \
            -DADB_VERSION='"$(DEB_VERSION)"' -D_GNU_SOURCE
LDFLAGS += -Wl,-rpath='$$ORIGIN/../lib/android' -Wl,-rpath-link='$$ORIGIN/../lib/android' \
           -lpthread -Ldebian/out/system/core -Ldebian/out/external/boringssl -lbase -lcrypto_utils -l:libcrypto.a -lcutils -llog -lresolv

PAGE_SIZE ?= 4096

CXXFLAGS += -UADB_HOST
CXXFLAGS +=	-DADB_HOST=0
CXXFLAGS += -DALLOW_ADBD_DISABLE_VERITY
CXXFLAGS += -DALLOW_ADBD_NO_AUTH
CXXFLAGS += -DPLATFORM_TOOLS_VERSION='"28.0.2"' 
CXXFLAGS += -Isystem/core/diagnose_usb/include 
CXXFLAGS += -Isystem/core/adb/daemon/include
CXXFLAGS += -Isystem/core/adb/adbconnection/include
CXXFLAGS += -Isystem/core/libasyncio/include
CXXFLAGS += -Isystem/core/libcutils/include
CXXFLAGS += -Isystem/core/libcrypto_utils/include
CXXFLAGS += -Isystem/core/liblog/include/
CXXFLAGS += -Isystem/core/libutils/include
CXXFLAGS += -Iframeworks/native/libs/adbd_auth/include
CXXFLAGS += -Wno-c++11-narrowing 
CXXFLAGS += -DPAGE_SIZE=$(PAGE_SIZE)


# -latomic should be the last library specified
# https://github.com/android/ndk/issues/589
ifneq ($(filter armel mipsel,$(DEB_HOST_ARCH)),)
  LDFLAGS += -latomic
endif

build: $(SOURCES)
	mkdir --parents debian/out/system/core
	$(CXX) $^ -o debian/out/system/core/adbd $(CXXFLAGS) $(CPPFLAGS) $(LDFLAGS)

clean:
	$(RM) debian/out/system/core/adbd
