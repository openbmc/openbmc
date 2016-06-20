#
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Toaster Implementation
#
# Copyright (C) 2013        Intel Corporation
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

# Django settings for Toaster project.

import os, re

DEBUG = True
TEMPLATE_DEBUG = DEBUG

# Set to True to see the SQL queries in console
SQL_DEBUG = False
if os.environ.get("TOASTER_SQLDEBUG", None) is not None:
    SQL_DEBUG = True


ADMINS = (
    # ('Your Name', 'your_email@example.com'),
)

MANAGERS = ADMINS

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.sqlite3', # Add 'postgresql_psycopg2', 'mysql', 'sqlite3' or 'oracle'.
        'NAME': 'toaster.sqlite',                      # Or path to database file if using sqlite3.
        'USER': '',
        'PASSWORD': '',
        'HOST': '127.0.0.1',                      # Empty for localhost through domain sockets or '127.0.0.1' for localhost through TCP.
        'PORT': '3306',                      # Set to empty string for default.
    }
}

# Needed when Using sqlite especially to add a longer timeout for waiting
# for the database lock to be  released
# https://docs.djangoproject.com/en/1.6/ref/databases/#database-is-locked-errors
if 'sqlite' in DATABASES['default']['ENGINE']:
    DATABASES['default']['OPTIONS'] = { 'timeout': 20 }

# Reinterpret database settings if we have DATABASE_URL environment variable defined

if 'DATABASE_URL' in os.environ:
    dburl = os.environ['DATABASE_URL']

    if dburl.startswith('sqlite3://'):
        result = re.match('sqlite3://(.*)', dburl)
        if result is None:
            raise Exception("ERROR: Could not read sqlite database url: %s" % dburl)
        DATABASES['default'] = {
            'ENGINE': 'django.db.backends.sqlite3',
            'NAME': result.group(1),
            'USER': '',
            'PASSWORD': '',
            'HOST': '',
            'PORT': '',
        }
    elif dburl.startswith('mysql://'):
        # URL must be in this form: mysql://user:pass@host:port/name
        result = re.match(r"mysql://([^:]*):([^@]*)@([^:]*):(\d*)/([^/]*)", dburl)
        if result is None:
            raise Exception("ERROR: Could not read mysql database url: %s" % dburl)
        DATABASES['default'] = {
            'ENGINE': 'django.db.backends.mysql',
            'NAME': result.group(5),
            'USER': result.group(1),
            'PASSWORD': result.group(2),
            'HOST': result.group(3),
            'PORT': result.group(4),
        }
    else:
        raise Exception("FIXME: Please implement missing database url schema for url: %s" % dburl)


# Allows current database settings to be exported as a DATABASE_URL environment variable value

def getDATABASE_URL():
    d = DATABASES['default']
    if d['ENGINE'] == 'django.db.backends.sqlite3':
        if d['NAME'] == ':memory:':
            return 'sqlite3://:memory:'
        elif d['NAME'].startswith("/"):
            return 'sqlite3://' + d['NAME']
        return "sqlite3://" + os.path.join(os.getcwd(), d['NAME'])

    elif d['ENGINE'] == 'django.db.backends.mysql':
        return "mysql://" + d['USER'] + ":" + d['PASSWORD'] + "@" + d['HOST'] + ":" + d['PORT'] + "/" + d['NAME']

    raise Exception("FIXME: Please implement missing database url schema for engine: %s" % d['ENGINE'])



# Hosts/domain names that are valid for this site; required if DEBUG is False
# See https://docs.djangoproject.com/en/1.5/ref/settings/#allowed-hosts
ALLOWED_HOSTS = []

# Local time zone for this installation. Choices can be found here:
# http://en.wikipedia.org/wiki/List_of_tz_zones_by_name
# although not all choices may be available on all operating systems.
# In a Windows environment this must be set to your system time zone.

# Always use local computer's time zone, find
import hashlib
if 'TZ' in os.environ:
    TIME_ZONE = os.environ['TZ']
else:
    # need to read the /etc/localtime file which is the libc standard
    # and do a reverse-mapping to /usr/share/zoneinfo/;
    # since the timezone may match any number of identical timezone definitions,

    zonefilelist = {}
    ZONEINFOPATH = '/usr/share/zoneinfo/'
    for dirpath, dirnames, filenames in os.walk(ZONEINFOPATH):
        for fn in filenames:
            filepath = os.path.join(dirpath, fn)
            zonename = filepath.lstrip(ZONEINFOPATH).strip()
            try:
                import pytz
                from pytz.exceptions import UnknownTimeZoneError
                pass
                try:
                    if pytz.timezone(zonename) is not None:
                        zonefilelist[hashlib.md5(open(filepath).read()).hexdigest()] = zonename
                except UnknownTimeZoneError, ValueError:
                    # we expect timezone failures here, just move over
                    pass
            except ImportError:
                zonefilelist[hashlib.md5(open(filepath).read()).hexdigest()] = zonename

    TIME_ZONE = zonefilelist[hashlib.md5(open('/etc/localtime').read()).hexdigest()]

# Language code for this installation. All choices can be found here:
# http://www.i18nguy.com/unicode/language-identifiers.html
LANGUAGE_CODE = 'en-us'

SITE_ID = 1

# If you set this to False, Django will make some optimizations so as not
# to load the internationalization machinery.
USE_I18N = True

# If you set this to False, Django will not format dates, numbers and
# calendars according to the current locale.
USE_L10N = True

# If you set this to False, Django will not use timezone-aware datetimes.
USE_TZ = True

# Absolute filesystem path to the directory that will hold user-uploaded files.
# Example: "/var/www/example.com/media/"
MEDIA_ROOT = ''

# URL that handles the media served from MEDIA_ROOT. Make sure to use a
# trailing slash.
# Examples: "http://example.com/media/", "http://media.example.com/"
MEDIA_URL = ''

# Absolute path to the directory static files should be collected to.
# Don't put anything in this directory yourself; store your static files
# in apps' "static/" subdirectories and in STATICFILES_DIRS.
# Example: "/var/www/example.com/static/"
STATIC_ROOT = ''

# URL prefix for static files.
# Example: "http://example.com/static/", "http://static.example.com/"
STATIC_URL = '/static/'

# Additional locations of static files
STATICFILES_DIRS = (
    # Put strings here, like "/home/html/static" or "C:/www/django/static".
    # Always use forward slashes, even on Windows.
    # Don't forget to use absolute paths, not relative paths.
)

# List of finder classes that know how to find static files in
# various locations.
STATICFILES_FINDERS = (
    'django.contrib.staticfiles.finders.FileSystemFinder',
    'django.contrib.staticfiles.finders.AppDirectoriesFinder',
#    'django.contrib.staticfiles.finders.DefaultStorageFinder',
)

# Make this unique, and don't share it with anybody.
SECRET_KEY = 'NOT_SUITABLE_FOR_HOSTED_DEPLOYMENT'

# List of callables that know how to import templates from various sources.
TEMPLATE_LOADERS = (
    'django.template.loaders.filesystem.Loader',
    'django.template.loaders.app_directories.Loader',
#     'django.template.loaders.eggs.Loader',
)

MIDDLEWARE_CLASSES = (
    'django.middleware.common.CommonMiddleware',
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    # Uncomment the next line for simple clickjacking protection:
    # 'django.middleware.clickjacking.XFrameOptionsMiddleware',
)

CACHES = {
    #        'default': {
    #            'BACKEND': 'django.core.cache.backends.memcached.MemcachedCache',
    #            'LOCATION': '127.0.0.1:11211',
    #        },
           'default': {
               'BACKEND': 'django.core.cache.backends.filebased.FileBasedCache',
               'LOCATION': '/tmp/toaster_cache_%d' % os.getuid(),
               'TIMEOUT': 1,
            }
          }


from os.path import dirname as DN
SITE_ROOT=DN(DN(os.path.abspath(__file__)))

import subprocess
TOASTER_BRANCH = subprocess.Popen('git branch | grep "^* " | tr -d "* "', cwd = SITE_ROOT, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()[0]
TOASTER_REVISION = subprocess.Popen('git rev-parse HEAD ', cwd = SITE_ROOT, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE).communicate()[0]

ROOT_URLCONF = 'toastermain.urls'

# Python dotted path to the WSGI application used by Django's runserver.
WSGI_APPLICATION = 'toastermain.wsgi.application'

TEMPLATE_DIRS = (
    # Put strings here, like "/home/html/django_templates" or "C:/www/django/templates".
    # Always use forward slashes, even on Windows.
    # Don't forget to use absolute paths, not relative paths.
)

TEMPLATE_CONTEXT_PROCESSORS = ('django.contrib.auth.context_processors.auth',
 'django.core.context_processors.debug',
 'django.core.context_processors.i18n',
 'django.core.context_processors.media',
 'django.core.context_processors.static',
 'django.core.context_processors.tz',
 'django.contrib.messages.context_processors.messages',
 "django.core.context_processors.request",
 'toastergui.views.managedcontextprocessor',
 )

INSTALLED_APPS = (
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.messages',
    'django.contrib.sessions',
    'django.contrib.admin',
    'django.contrib.staticfiles',

    # Uncomment the next line to enable admin documentation:
    # 'django.contrib.admindocs',
    'django.contrib.humanize',
    'bldcollector',
    'toastermain',
)


INTERNAL_IPS = ['127.0.0.1', '192.168.2.28']

# Load django-fresh is TOASTER_DEVEL is set, and the module is available
FRESH_ENABLED = False
if os.environ.get('TOASTER_DEVEL', None) is not None:
    try:
        import fresh
        MIDDLEWARE_CLASSES = ("fresh.middleware.FreshMiddleware",) + MIDDLEWARE_CLASSES
        INSTALLED_APPS = INSTALLED_APPS + ('fresh',)
        FRESH_ENABLED = True
    except:
        pass

DEBUG_PANEL_ENABLED = False
if os.environ.get('TOASTER_DEVEL', None) is not None:
    try:
        import debug_toolbar, debug_panel
        MIDDLEWARE_CLASSES = ('debug_panel.middleware.DebugPanelMiddleware',) + MIDDLEWARE_CLASSES
        #MIDDLEWARE_CLASSES = MIDDLEWARE_CLASSES + ('debug_toolbar.middleware.DebugToolbarMiddleware',)
        INSTALLED_APPS = INSTALLED_APPS + ('debug_toolbar','debug_panel',)
        DEBUG_PANEL_ENABLED = True

        # this cache backend will be used by django-debug-panel
        CACHES['debug-panel'] = {
                'BACKEND': 'django.core.cache.backends.filebased.FileBasedCache',
                'LOCATION': '/var/tmp/debug-panel-cache',
                'TIMEOUT': 300,
                'OPTIONS': {
                    'MAX_ENTRIES': 200
                }
        }

    except:
        pass


SOUTH_TESTS_MIGRATE = False


# We automatically detect and install applications here if
# they have a 'models.py' or 'views.py' file
import os
currentdir = os.path.dirname(__file__)
for t in os.walk(os.path.dirname(currentdir)):
    modulename = os.path.basename(t[0])
    #if we have a virtualenv skip it to avoid incorrect imports
    if os.environ.has_key('VIRTUAL_ENV') and os.environ['VIRTUAL_ENV'] in t[0]:
        continue

    if ("views.py" in t[2] or "models.py" in t[2]) and not modulename in INSTALLED_APPS:
        INSTALLED_APPS = INSTALLED_APPS + (modulename,)

# A sample logging configuration. The only tangible logging
# performed by this configuration is to send an email to
# the site admins on every HTTP 500 error when DEBUG=False.
# See http://docs.djangoproject.com/en/dev/topics/logging for
# more details on how to customize your logging configuration.
LOGGING = {
    'version': 1,
    'disable_existing_loggers': False,
    'filters': {
        'require_debug_false': {
            '()': 'django.utils.log.RequireDebugFalse'
        }
    },
    'formatters': {
        'datetime': {
            'format': '%(asctime)s %(levelname)s %(message)s'
        }
    },
    'handlers': {
        'mail_admins': {
            'level': 'ERROR',
            'filters': ['require_debug_false'],
            'class': 'django.utils.log.AdminEmailHandler'
        },
        'console': {
            'level': 'DEBUG',
            'class': 'logging.StreamHandler',
            'formatter': 'datetime',
        }
    },
    'loggers': {
        'toaster' : {
            'handlers': ['console'],
            'level': 'DEBUG',
        },
        'django.request': {
            'handlers': ['console'],
            'level': 'WARN',
            'propagate': True,
        },
    }
}

if DEBUG and SQL_DEBUG:
    LOGGING['loggers']['django.db.backends'] = {
            'level': 'DEBUG',
            'handlers': ['console'],
        }


# If we're using sqlite, we need to tweak the performance a bit
from django.db.backends.signals import connection_created
def activate_synchronous_off(sender, connection, **kwargs):
    if connection.vendor == 'sqlite':
        cursor = connection.cursor()
        cursor.execute('PRAGMA synchronous = 0;')
connection_created.connect(activate_synchronous_off)
#


class InvalidString(str):
    def __mod__(self, other):
        from django.template.base import TemplateSyntaxError
        raise TemplateSyntaxError(
            "Undefined variable or unknown value for: \"%s\"" % other)

TEMPLATE_STRING_IF_INVALID = InvalidString("%s")
