SUMMARY = "Advanced Python Scheduler (APScheduler) is a Python library that lets you schedule your Python code to be executed later, either just once or periodically."
HOMEPAGE = "https://github.com/agronholm/apscheduler"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f0e423eea5c91e7aa21bdb70184b3e53"

SRC_URI[sha256sum] = "e6df071b27d9be898e486bc7940a7be50b4af2e9da7c08f0744a96d4bd4cef4a"

PYPI_PACKAGE = "APScheduler"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"
