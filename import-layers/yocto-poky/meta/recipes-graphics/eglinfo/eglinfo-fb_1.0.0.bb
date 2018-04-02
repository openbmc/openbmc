EGLINFO_PLATFORM ?= "fb"
EGLINFO_BINARY_NAME ?= "eglinfo-fb"

require eglinfo.inc

SUMMARY += "(Framebuffer version)"
CXXFLAGS += "-DMESA_EGL_NO_X11_HEADERS=1"
