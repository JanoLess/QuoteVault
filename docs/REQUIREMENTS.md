# QuoteVault requirements (BA)

## App idea

A small quote browser. A person can read quotes, search them, save favorites, and keep using the last downloaded list when the network is down.

## User stories

1. As a reader, I want to see a list of quotes so I can browse something useful on my phone.
2. As a reader, I want to search by text or author so I can find a quote quickly.
3. As a reader, I want to open a quote on its own screen so I can read it without the list around it.
4. As a reader, I want to save and remove favorites so I can come back to quotes I like.
5. As a reader, I want a dark mode setting that stays saved on this device.
6. As a reader, I want the last successful quote list to stay available offline.

## Screen flow

```
Browse  --tap quote-->  Detail
   |                      |
Favorites --tap quote-->  Detail
   |
Settings (dark mode)
```

Bottom tabs: Browse, Favorites, Settings. Detail is pushed on the back stack and has a Back action.

## Acceptance criteria

- At least three screens: Browse, Favorites, Settings (Detail is a fourth screen that receives a quote id).
- Browse shows Loading, a quote list, or an error with Try again.
- Search filters the current list without crashing on an empty query.
- Favorite hearts update Browse, Favorites, and Detail from the same shared state.
- Dark mode survives closing and reopening the app.
- After one successful network load, turning on airplane mode and opening the app still shows quotes.
- The first load with no cache and no network shows an error, not a crash.
- Android app builds from the README instructions.

## Wireframe (text)

Browse: title, search field, Refresh button, scrolling quote cards with heart icons.

Favorites: title, count, empty state or the same card list.

Detail: full quote, author, save/remove button, Back.

Settings: Dark mode switch and a short note about offline cache.
