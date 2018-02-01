#
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Toaster Implementation
#
# Copyright (C) 2016        Intel Corporation
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

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
