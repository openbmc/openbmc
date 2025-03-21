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
    atomic \
    chrono \
    context \
    coroutine \
    filesystem \
    process \
    thread \
    url \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'serialization', '', d)} \
"
# When libcereal is compiled with ptest, it needs boost to support
# 'serialization'.

# etcd requires some additional support.
BOOST_LIBS:openbmc-phosphor:class-target:append:df-etcd = " \
    date_time \
    random \
    regex \
    system \
"
