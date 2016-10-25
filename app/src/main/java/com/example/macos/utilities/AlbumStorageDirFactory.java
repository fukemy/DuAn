package com.example.macos.utilities;

import java.io.File;

abstract class AlbumStorageDirFactory {
	public abstract File getAlbumStorageDir(String albumName);
}
