#
# BitBake Toaster Implementation
#
# Copyright (C) 2013        Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

from django.conf.urls import include, url
from django.views.generic import RedirectView, TemplateView
from django.views.decorators.cache import never_cache
import bldcollector.views

import logging

logger = logging.getLogger("toaster")

# Uncomment the next two lines to enable the admin:
from django.contrib import admin
admin.autodiscover()

urlpatterns = [

    # Examples:
    # url(r'^toaster/', include('toaster.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),


    # This is here to maintain backward compatibility and will be deprecated
    # in the future.
    url(r'^orm/eventfile$', bldcollector.views.eventfile),

    url(r'^health$', TemplateView.as_view(template_name="health.html"), name='Toaster Health'),

    # if no application is selected, we have the magic toastergui app here
    url(r'^$', never_cache(RedirectView.as_view(url='/toastergui/', permanent=True))),
]

import toastermain.settings

if toastermain.settings.FRESH_ENABLED:
    urlpatterns.insert(1, url(r'', include('fresh.urls')))
    #logger.info("Enabled django-fresh extension")

if toastermain.settings.DEBUG_PANEL_ENABLED:
    import debug_toolbar
    urlpatterns.insert(1, url(r'', include(debug_toolbar.urls)))
    #logger.info("Enabled django_toolbar extension")

urlpatterns = [
    # Uncomment the next line to enable the admin:
    url(r'^admin/', include(admin.site.urls)),
] + urlpatterns

# Automatically discover urls.py in various apps, beside our own
# and map module directories to the patterns

import os
currentdir = os.path.dirname(__file__)
for t in os.walk(os.path.dirname(currentdir)):
    #if we have a virtualenv skip it to avoid incorrect imports
    if 'VIRTUAL_ENV' in os.environ and os.environ['VIRTUAL_ENV'] in t[0]:
        continue

    if "urls.py" in t[2] and t[0] != currentdir:
        modulename = os.path.basename(t[0])
        # make sure we don't have this module name in
        conflict = False
        for p in urlpatterns:
            if p.regex.pattern == '^' + modulename + '/':
                conflict = True
        if not conflict:
            urlpatterns.insert(0, url(r'^' + modulename + '/', include ( modulename + '.urls')))
        else:
            logger.warning("Module \'%s\' has a regexp conflict, was not added to the urlpatterns" % modulename)

from pprint import pformat
#logger.debug("urlpatterns list %s", pformat(urlpatterns))
