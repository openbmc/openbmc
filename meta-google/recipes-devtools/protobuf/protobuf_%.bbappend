# Reduces the size of the output binaries by ~300K
CFLAGS:append:class-target = " -flto"
CXXFLAGS:append:class-target = " -flto"
