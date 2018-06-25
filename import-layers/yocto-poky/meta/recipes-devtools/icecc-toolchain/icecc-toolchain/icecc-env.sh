#! /bin/sh
# Copyright (c) 2018 Joshua Watt, Garmin International,Inc.
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.
#

if [ -z "$ICECC_PATH" ]; then
    ICECC_PATH=$(which icecc 2> /dev/null)
fi

if [ -n "$ICECC_PATH" ]; then
    # Default to disabling the caret workaround. If set to "1", icecc will
    # locally recompile any files that have warnings, which can adversely
    # affect performance.
    #
    # See: https://github.com/icecc/icecream/issues/190
    if [ -z "$ICECC_CARET_WORKAROUND" ]; then
        ICECC_CARET_WORKAROUND="0"
    fi
    if [ "$ICECC_CARET_WORKAROUND" != "1" ]; then
        CFLAGS="$CFLAGS -fno-diagnostics-show-caret"
        CXXFLAGS="$CXXFLAGS -fno-diagnostics-show-caret"
    fi
    export ICECC_PATH ICECC_CARET_WORKAROUND
    export ICECC_VERSION="$OECORE_NATIVE_SYSROOT/usr/share/icecream/@TOOLCHAIN_ENV@"
    export ICECC="$(which ${CROSS_COMPILE}gcc)"
    export ICECXX="$(which ${CROSS_COMPILE}g++)"
    export ICEAS="$(which ${CROSS_COMPILE}as)"
    export PATH="$OECORE_NATIVE_SYSROOT/usr/share/icecream/bin:$PATH"
else
    echo "Icecc not found. Disabling distributed compiling"
fi

