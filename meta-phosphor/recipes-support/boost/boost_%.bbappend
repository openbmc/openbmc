#This is largely to improve our build times by not building or installing
#modules that OpenBMC does not use by our coding standard.  Chrono and Thread
#should not be relied directly, but are required dependencies of context and
#coroutine at build time.  See the relevant portion of the openbmc coding
#standard with regards to boost libraries
#
#https://github.com/openbmc/docs/blob/master/cpp-style-and-conventions.md#boost
#
BOOST_LIBS_openbmc-phosphor = "chrono context coroutine thread"

