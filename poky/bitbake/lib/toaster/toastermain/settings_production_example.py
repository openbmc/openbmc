#
# BitBake Toaster Implementation
#
# Copyright (C) 2016        Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

# See Django documentation for more information about deployment
# https://docs.djangoproject.com/en/1.8/howto/deployment/checklist/

# Toaster production settings example overlay
# To use this copy this example to "settings_production.py" and set in your
# environment DJANGO_SETTINGS_MODULE=toastermain.settings_production
# This can be permanently set in a new .wsgi file

from toastermain.settings import *  # NOQA

# Set this value!
SECRET_KEY = None

# Switch off any debugging
DEBUG = True
TEMPLATE_DEBUG = DEBUG

DATABASES = {
     'default': {
         'ENGINE': 'django.db.backends.mysql',
         'NAME': 'toaster_data',
         'USER': 'toaster',
         'PASSWORD': 'yourpasswordhere',
         'HOST': '127.0.0.1',
         'PORT': '3306',
     }
}

# Location where static files will be placed by "manage.py collectstatic"
STATIC_ROOT = '/var/www/static-toaster/'

# URL prefix for static files.
STATIC_URL = '/static-toaster/'

# Hosts that Django will serve
# https://docs.djangoproject.com/en/1.8/ref/settings/#std:setting-ALLOWED_HOSTS
ALLOWED_HOSTS = ['toaster-example.example.com']
