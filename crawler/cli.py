"""Command line interface for the crawler.

Provides a command line interface for the REST API. It simply forwards
the calls to the REST endpoint.
"""


# Python imports
import json
import argparse
from sys import exit


# 3rd party modules
import requests


# Local imports
import crawler.services.environment as environment


_API = None


def check_connection() -> bool:
    """Helper method to check if the crawler is up and running.

    Returns:
        bool: True if connections are able to establish, False otherwise

    """
    url = f'{_API}'
    try:
        response = requests.get(url)
    except requests.ConnectionError:
        print(
            'Unable to establish connection to the crawler API.\n'
            'Please make sure the crawler is up and running and try again.'
        )
        return False
    return True


def make_request(url: str, post: bool = False, json_output: bool = False) -> None:
    """Helper function to make a request to the crawler API.

    Args:
        url (str): url to request
        post (bool, optional): use POST instead of GET. Defaults to False.

    """
    if post:
        response = requests.post(url)
    else:
        response = requests.get(url)
    data = json.loads(response.text or '{}')
    if response.ok:
        res = data.get('message', 'OK')
    else:
        res = data.get('error', 'Unavailable to load error message')
    if json_output:
        print(json.dumps(res, indent=4))
    else:
        print(res)


def info(subparser: argparse._SubParsersAction = None):
    """Forward to /info

    Args:
        subparser (argparse._SubParsersAction, optional):
            Parser for command 'info'. Defaults to None.

    """
    name = 'info'
    if subparser is None:
        make_request(f'{_API}/{name}', json_output=True)
        return
    subparser.add_parser(
        name,
        description='Get information about the current status of the crawler'
    )


def stop(
    subparser: argparse._SubParsersAction = None
):
    """Forward to /stop

    Args:
        subparser (argparse._SubParsersAction, optional):
            Parser for command 'stop'. Defaults to None.

    """
    name = 'stop'
    if subparser is None:
        make_request(f'{_API}/{name}', True)
        return
    subparser.add_parser(
        name,
        description='Stop the current execution of the crawler'
    )


def start(
    subparser: argparse._SubParsersAction = None,
    args: argparse.Namespace = None
):
    """Forward to /start?config={config}

    Args:
        subparser (argparse._SubParsersAction, optional):
            Parser for command 'start'. Defaults to None.
        args (argparse.Namespace, optional):
            Arguments of command 'start'. Defaults to None.

    """
    name = 'start'
    if subparser is None:
        make_request(f'{_API}/{name}?config={args.config}&update={args.update}', True)
        return
    parser = subparser.add_parser(
        name,
        description='Start the crawler with given configuration'
    )
    parser.add_argument(
        'config',
        type=str,
        metavar='config',
        help=(
            'Configuration of the crawler execution. '
            'Either a valid JSON configuration itself or a '
            'filepath to a valid configuration file'
        )
    )
    parser.add_argument(
        'update',
        default='false',
        type=str,
        metavar='update',
        help=(
            'Update a possibly running execution. '
            'Passing \'true\' will stop the current execution and start the new one. '
            'Ignoring that value will ignore the new config if the TreeWalk '
            'is currently running.'
        )
    )


def pause(
    subparser: argparse._SubParsersAction = None
):
    """Forward to /pause

    Args:
        subparser (argparse._SubParsersAction, optional):
            Parser for command 'pause'. Defaults to None.

    """
    name = 'pause'
    if subparser is None:
        make_request(f'{_API}/{name}', True)
        return
    subparser.add_parser(
        name,
        description='Pause the current execution of the crawler'
    )


def unpause(
    subparser: argparse._SubParsersAction = None
):
    """Forward to /unpause

    Args:
        subparser (argparse._SubParsersAction, optional):
            Parser for command 'unpause'. Defaults to None.

    """
    name = 'continue'
    if subparser is None:
        make_request(f'{_API}/{name}', True)
        return
    subparser.add_parser(
        name,
        description='Continue the current execution of the crawler'
    )


def shutdown(
    subparser: argparse._SubParsersAction = None
):
    """Forward to /shutdown

    Args:
        subparser (argparse._SubParsersAction, optional):
            Parser for command 'shutdown'. Defaults to None.

    """
    name = 'shutdown'
    if subparser is None:
        requests.post(f'{_API}/{name}')
        return
    subparser.add_parser(
        name,
        description='Shutdown the crawler'
    )


if __name__ == '__main__':
    try:
        environment.init()
    except environment.InvalidEnvironmentException as err:
        print(f'{str(err)} Aborted.')
        exit(1)
    _API =f'http://{environment.env.CRAWLER_HOST}:{environment.env.CRAWLER_PORT}'
    parser = argparse.ArgumentParser()
    subparser = parser.add_subparsers(title='command', dest='command')
    # Create parsers for each command
    info(subparser=subparser)
    stop(subparser=subparser)
    start(subparser=subparser)
    pause(subparser=subparser)
    unpause(subparser=subparser)
    shutdown(subparser=subparser)
    args = parser.parse_args()
    # Check connection and run command
    if not check_connection():
        exit(1)
    if args.command == 'info':
        info()
    if args.command == 'stop':
        stop()
    if args.command == 'start':
        start(args=args)
    if args.command == 'pause':
        pause()
    if args.command == 'continue':
        unpause()
    if args.command == 'shutdown':
        shutdown()
