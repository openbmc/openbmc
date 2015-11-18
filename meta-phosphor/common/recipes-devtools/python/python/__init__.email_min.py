try:
	import email_full

except ImportError:
	import sys
	import email.utils
	sys.modules['email.Utils'] = sys.modules['email.utils']
