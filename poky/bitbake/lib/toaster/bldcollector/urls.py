#
# BitBake Toaster Implementation
#
# Copyright (C) 2014-2017   Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

from django.conf.urls import include, url

import bldcollector.views

urlpatterns = [
        # landing point for pushing a bitbake_eventlog.json file to this toaster instace
        url(r'^eventfile$', bldcollector.views.eventfile, name='eventfile'),
]
