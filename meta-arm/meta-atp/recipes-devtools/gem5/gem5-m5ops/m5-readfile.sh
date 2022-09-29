#!/bin/sh
### BEGIN INIT INFO
# Provides:         m5-readfile
# Required-Start:   $all
# Default-Start:    5
# Description:      Enables reading any script at simulation launch time.
### END INIT INFO

m5 readfile | sh
