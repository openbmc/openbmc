include boost-${PV}.inc
include boost.inc

SRC_URI += "\
    file://arm-intrinsics.patch \
    file://consider-hardfp.patch \
    file://boost-CVE-2012-2677.patch \
    file://0001-boost-asio-detail-socket_types.hpp-fix-poll.h-includ.patch \
    file://0002-boost-test-execution_monitor.hpp-fix-mips-soft-float.patch \
    file://0003-smart_ptr-mips-assembly-doesn-t-compile-in-mips16e-m.patch \
    file://0004-Use-atomic-by-default-when-BOOST_NO_CXX11_HDR_ATOMIC.patch \
    file://boost-math-disable-pch-for-gcc.patch \
"
