PACKAGECONFIG:openbmc-phosphor:class-target = ""

#This is largely to improve our build times by not building or installing
#modules that OpenBMC does not use by our coding standard.  Another thing to
#note is that for most targets, coroutine and context libraries are also added
#with a BOOST_LIBS:append:<platform> for most targets.  Chrono/Thread should not
#be relied directly, but are required dependencies of context and coroutine.
#See the relevant portion of the openbmc coding standard with regards to boost
#libraries
#
#https://github.com/openbmc/docs/blob/master/cpp-style-and-conventions.md#boost
#
BOOST_LIBS:openbmc-phosphor:class-target = " \
    chrono \
    context \
    coroutine \
    date_time \
    process \
    thread \
    url \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'serialization', '', d)} \
"
# When libcereal is compiled with ptest, it needs boost to support
# 'serialization'.


# Openbmc applications should be setting BOOST_SYSTEM_NO_DEPRECATED
# For whatever reason, boost 1.87 has decided to install this library
# unconditionally.  It's not clear why, but just ignore it for now
# by categorizing it as a dev dependency
FILES:${PN}-dev:append = " \
    ${libdir}/libboost_system*.so.* \
"

BJAM_OPTS:append = " boost.process.fs=std"

# etcd requires some additional support.
BOOST_LIBS:openbmc-phosphor:class-target:append:df-etcd = " \
    random \
    regex \
    system \
"
