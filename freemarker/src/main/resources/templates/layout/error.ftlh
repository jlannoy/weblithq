<html lang="${lang!''}">
<head>
    <title>Weblith Auth0 simulator</title>
    <style type="text/css">
    body {
      background-color: #E0E0E0;
      font-family: Calibri, Tahoma, sans;
    }
    .center-div {
      position: absolute;
      margin: auto;
      top: 0;
      right: 0;
      bottom: 0;
      left: 0;
      width: 400px;
      height: 188px;
      padding: 20px;
      border: 1px solid #A0A0A0;
      border-radius: 10px;
      background-color: white;
      text-align: center;
    }
    hr {
        border: 0;
        height: 0;
        border-top: 1px solid #A0A0A0;
        width: 92%
    }
    a {
        color: white;
        text-decoration: none;
    }
    input {
        padding: 5px;
        border: 1px solid #aeaeae;
        border-radius: 5px;
        width: 90%;
        box-shadow: 1px 1px 2px #C0C0C0 inset;
        font-size: .9em;
        margin: 6px 0;
    }
    button {
        padding: 5px;
        border: 1px solid #aeaeae;
        border-radius: 5px;
        width: 90%;
        box-shadow: 1px 1px 2px #C0C0C0;
        font-size: 1em;
    }
    h1 {
        margin: 0;
    }
    p {
        font-size: 80%;
        color: #A0A0A0;
        margin: 0 0 6px;
    }
    </style>
</head>
<body>
    <div class="center-div">
        <h1>Error</h1>
        <p>${message!''}</p>
        <a href="javascript:history.back()" role="button">Go back</a>
    </div>
</body>
</html>