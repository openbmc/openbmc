#This is largely to improve our build times by not building or installing
#modules that OpenBMC does not use by our coding standard.  Another thing to
#note is that for most targets, coroutine and context libraries are also added
#with a BOOST_LIBS_append_<platform> for most targets.  Chrono/Thread should not
#be relied directly, but are required dependencies of context and coroutine.
#See the relevant portion of the openbmc coding standard with regards to boost
#libraries
#
#https://github.com/openbmc/docs/blob/master/cpp-style-and-conventions.md#boost
#
#BOOST_LIBS_openbmc-phosphor = "chrono  thread"

