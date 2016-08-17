SDK-EFL = " \
     efl-dev \
     elementary-dev \
     edbus-dev \
     luajit-dev \
"

SDK-EXTRAS ?= ""
SDK-EXTRAS_qemux86 ?= " valgrind lttng-ust-dev"
SDK-EXTRAS_qemux86-64 ?= " lttng-ust-dev"
SDK-EXTRAS_qemuppc ?= " lttng-ust-dev"
SDK-EXTRAS_qemuarm ?= " lttng-ust-dev"
