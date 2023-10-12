#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import logging
import json
from pathlib import Path
from django.http import HttpRequest

BASE_DIR = Path(__file__).resolve(strict=True).parent.parent


def log_api_request(request, response, view, logger_name='api'):
    """Helper function for LogAPIMixin"""

    repjson = {
        'view': view,
        'path': request.path,
        'method': request.method,
        'status': response.status_code
    }

    logger = logging.getLogger(logger_name)
    logger.info(
        json.dumps(repjson, indent=4, separators=(", ", " : "))
    )


def log_view_mixin(view):
    def log_view_request(*args, **kwargs):
        # get request from args else kwargs
        request = None
        if len(args) > 0:
            for req in args:
                if isinstance(req, HttpRequest):
                    request = req
                    break 
        elif request is None:
            request = kwargs.get('request')

        response = view(*args, **kwargs)
        log_api_request(
            request, response, request.resolver_match.view_name, 'toaster')
        return response
    return log_view_request



class LogAPIMixin:
    """Logs API requests

    tested with:
        - APIView
        - ModelViewSet
        - ReadOnlyModelViewSet
        - GenericAPIView

    Note: you can set `view_name` attribute in View to override get_view_name()
    """

    def get_view_name(self):
        if hasattr(self, 'view_name'):
            return self.view_name
        return super().get_view_name()

    def finalize_response(self, request, response, *args, **kwargs):
        log_api_request(request, response, self.get_view_name())
        return super().finalize_response(request, response, *args, **kwargs)


LOGGING_SETTINGS = {
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
        },
        'verbose': {
            'format': '{levelname} {asctime} {module} {name}.{funcName} {process:d} {thread:d} {message}',
            'datefmt': "%d/%b/%Y %H:%M:%S",
            'style': '{',
        },
        'api': {
            'format': '\n{levelname} {asctime} {name}.{funcName}:\n{message}',
            'style': '{'
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
        },
        'file_django': {
            'level': 'INFO',
            'class': 'logging.handlers.TimedRotatingFileHandler',
            'filename': BASE_DIR / 'logs/django.log',
            'when': 'D',  # interval type
            'interval': 1,  # defaults to 1
            'backupCount': 10,  # how many files to keep
            'formatter': 'verbose',
        },
        'file_api': {
            'level': 'INFO',
            'class': 'logging.handlers.TimedRotatingFileHandler',
            'filename': BASE_DIR / 'logs/api.log',
            'when': 'D',
            'interval': 1,
            'backupCount': 10,
            'formatter': 'verbose',
        },
        'file_toaster': {
            'level': 'INFO',
            'class': 'logging.handlers.TimedRotatingFileHandler',
            'filename': BASE_DIR / 'logs/toaster.log',
            'when': 'D',
            'interval': 1,
            'backupCount': 10,
            'formatter': 'verbose',
        },
    },
    'loggers': {
        'django.request': {
            'handlers': ['file_django', 'console'],
            'level': 'WARN',
            'propagate': True,
        },
        'django': {
            'handlers': ['file_django', 'console'],
            'level': 'WARNING',
            'propogate': True,
        },
        'toaster': {
            'handlers': ['file_toaster'],
            'level': 'INFO',
            'propagate': False,
        },
        'api': {
            'handlers': ['file_api'],
            'level': 'INFO',
            'propagate': False,
        }
    }
}
