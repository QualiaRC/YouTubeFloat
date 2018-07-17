# YouTubeFloat
<a href="#"><img src="https://img.shields.io/badge/Version-1.0.0-brightgreen.svg" alt="Latest version"></a>

## About
A simple Java application that displays YouTube videos in an undecorated, always on top window.

## To use
The program will initially prompt the user for a standard YouTube url. The program will then parse it into an embed URL, then load it into a webview.
Right clicking the close button when a video is loaded will take you back to the URL prompt.

## Issues
- Does not work with YouTube Livestreaming
- Due to the nature of YouTube's video player, the video quality automatically adjusts to the window size, which can cause stuttering.
- It's possible to go to pages other than the embedded video.
