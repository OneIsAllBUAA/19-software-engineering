# django_websocket_demo/routing.py
from channels.auth import AuthMiddlewareStack
from channels.routing import ProtocolTypeRouter, URLRouter
import demo.routing

application = ProtocolTypeRouter({
    # (http->django views is added by default)
    'websocket': AuthMiddlewareStack(
        URLRouter(
            demo.routing.websocket_urlpatterns
        )
    ),
})
ASGI_APPLICATION = "myproject.routing.application"