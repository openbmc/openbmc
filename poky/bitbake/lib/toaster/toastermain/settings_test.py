#
# BitBake Toaster Implementation
#
# Copyright (C) 2016        Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

# Django settings for Toaster project.

# Settings overlay to use for running tests
# DJANGO_SETTINGS_MODULE=toastermain.settings-test

from toastermain.settings import *

DEBUG = True
TEMPLATE_DEBUG = DEBUG

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.sqlite3',
        'NAME': '%s/toaster-test-db.sqlite' % TMPDIR,
        'TEST': {
            'ENGINE': 'django.db.backends.sqlite3',
            'NAME': '%s/toaster-test-db.sqlite' % TMPDIR,
        }
    }
}
