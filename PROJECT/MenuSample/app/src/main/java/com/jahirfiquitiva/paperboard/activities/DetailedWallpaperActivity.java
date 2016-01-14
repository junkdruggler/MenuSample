package com.jahirfiquitiva.paperboard.activities;

import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.junkdruggler.sample.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class DetailedWallpaperActivity extends AppCompatActivity {

    private String saveWallLocation, picName, dialogContent, wall;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            postponeEnterTransition();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_wallpaper);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(R.string.title_ab_detailed_wallpaper);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        saveWallLocation = Environment.getExternalStorageDirectory().getAbsolutePath()
                + getResources().getString(R.string.walls_save_location);
        picName = getResources().getString(R.string.walls_prefix_name);

        dialogContent = getResources().getString(R.string.download_done) + saveWallLocation;

        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isfirstrun", true);

        if (isFirstRun) {
            File folder = new File(saveWallLocation);
            if (!folder.exists())
                folder.mkdirs();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putBoolean("isfirstrun", false).commit();

        }

        image = (ImageView) findViewById(R.id.bigwall);
        wall = getIntent().getStringExtra("wall");
        Picasso.with(this)
                .load(wall)
                .into(image, new Callback.EmptyCallback() {
                            @Override
                            public void onSuccess() {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                    startPostponedEnterTransition();
                            }
                        }
                );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_walls, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.download:
                downloadImage();
                break;
            case R.id.sharewall:
                shareRecipe();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void downloadImage(){
        Picasso.with(this)
                .load(wall)
                .into(target);
        showDownloadDialog(false);
    }

    //TODO: Make the share more smart by finding out which recipe has already been downloaded
    private void shareRecipe() {
        // Get access to bitmap image from view
        ImageView ivImage = (ImageView) findViewById(R.id.bigwall);
        // Get access to the URI for the bitmap

        Uri bmpUri = findLocalBitmapUri(ivImage);
        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            // Launch sharing dialog for image
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else {
            // ...sharing failed, handle error
        }
    }
        // Returns the URI path to the Bitmap displayed in specified ImageView
        public Uri findLocalBitmapUri(ImageView imageView) {
            // Extract Bitmap from ImageView drawable
            Drawable drawable = imageView.getDrawable();
            Bitmap bmp = null;
            if (drawable instanceof BitmapDrawable){
                bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            } else {
                return null;
            }
            // Store image to default external storage directory
            Uri bmpUri = null;
            try {
                File file =  new File(saveWallLocation, "share_image_" + System.currentTimeMillis() + ".png");
                file.getParentFile().mkdirs();
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                bmpUri = Uri.fromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmpUri;
        }


    private final com.squareup.picasso.Target target = new com.squareup.picasso.Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(saveWallLocation, picName + convertWallName(wall) + ".png");
                    file.delete();
                    try {
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                        ostream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            showNoPicDialog();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    private final com.squareup.picasso.Target wallTarget = new com.squareup.picasso.Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        WallpaperManager wm = WallpaperManager.getInstance(DetailedWallpaperActivity.this);
                        wm.setBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            showNoPicDialog();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    private final com.squareup.picasso.Target wallCropTarget = new com.squareup.picasso.Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ImageView wall = (ImageView) findViewById(R.id.bigwall);
                        Uri wallUri = getLocalBitmapUri(wall);
                        if (wallUri != null) {
                            Intent setWall = new Intent(Intent.ACTION_ATTACH_DATA);
                            setWall.setDataAndType(wallUri, "image/*");
                            setWall.putExtra("png", "image/*");
                            startActivityForResult(Intent.createChooser(setWall, getString(R.string.set_as)), 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            showNoPicDialog();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };


    /**
     * Taken from Apache Commons IO https://commons.apache.org/proper/commons-io/xref/org/apache/commons/io/FilenameUtils.html
     * Gets the base name, minus the full path and extension, from a full filename.
     * <p>
     * This method will handle a file in either Unix or Windows format.
     * The text after the last forward or backslash and before the last dot is returned.
      <pre>
     * a/b/c.txt --&gt; c
     * a.txt     --&gt; a
     * a/b/c     --&gt; c
     * a/b/c/    --&gt; ""
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     *
     * @param filename  the filename to query, null returns null
     * @return the name of the file without the path, or an empty string if none exists
     */

    public static final char EXTENSION_SEPARATOR = '.';
    private static final char UNIX_SEPARATOR = '/';
    private static final char WINDOWS_SEPARATOR = '\\';

    public static String getBaseName(final String filename) {
        return removeExtension(getName(filename));
    }
    public static String getName(final String filename) {
        if (filename == null) {
            return null;
        }
        final int index = indexOfLastSeparator(filename);
        return filename.substring(index + 1);
    }

    public static String removeExtension(final String filename) {
        if (filename == null) {
            return null;
        }
        final int index = indexOfExtension(filename);
        if (index == -1) {
            return filename;
        } else {
            return filename.substring(0, index);
        }
    }
    public static int indexOfLastSeparator(final String filename) {
        if (filename == null) {
            return -1;
        }
        final int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        final int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }

    public static int indexOfExtension(final String filename) {
        if (filename == null) {
            return -1;
        }
        final int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
        final int lastSeparator = indexOfLastSeparator(filename);
        return lastSeparator > extensionPos ? -1 : extensionPos;
    }

    private String convertWallName(String link) {
        return (getBaseName(link));
        /*return (link
                .replaceAll("png", "")                   // Deletes png extension
                .replaceAll("jpg", "")                   // Deletes jpg extension
                .replaceAll("jpeg", "")                  // Deletes jpeg extension
                .replaceAll("bmp", "")                   // Deletes bmp extension
                .replaceAll("[^a-zA-Z0-9\\p{Z}]", "")    // Remove all special characters and symbols
                .replaceFirst("^[0-9]+(?!$)", "")        // Remove all leading numbers unless they're all numbers
                .replaceAll("\\p{Z}", "_"))              // Replace all kinds of spaces with underscores
                .replaceAll(getResources().getString(R.string.replace_one), "")
                .replaceAll(getResources().getString(R.string.replace_two), "")
                .replaceAll(getResources().getString(R.string.replace_three), "")
                .replaceAll(getResources().getString(R.string.replace_four), "")
                .replaceAll(getResources().getString(R.string.replace_five), "")
                .replaceAll(getResources().getString(R.string.replace_six), "")
                .replaceAll(getResources().getString(R.string.replace_seven), "");*/

    }

    public void showDownloadDialog(boolean indeterminate) {
        if (indeterminate) {
            new MaterialDialog.Builder(this)
                    .title(R.string.progress_dialog_title)
                    .content(R.string.please_wait)
                    .progress(true, 0)
                    .show();
        } else {
            new MaterialDialog.Builder(this)
                    .title(R.string.progress_dialog_title)
                    .content(R.string.please_wait)
                    .progress(false, 120)
                    .showListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            final MaterialDialog dialog = (MaterialDialog) dialogInterface;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (dialog.getCurrentProgress() != dialog.getMaxProgress()) {
                                        if (dialog.isCancelled())
                                            break;
                                        try {
                                            Thread.sleep(50);
                                        } catch (InterruptedException e) {
                                            break;
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.incrementProgress(1);
                                            }
                                        });
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.setTitle(getString(R.string.done));
                                            dialog.setContent(dialogContent);
                                            dialog.setActionButton(DialogAction.NEGATIVE, R.string.close);
                                        }
                                    });
                                }
                            }).start();
                        }
                    }).show();
        }
    }

    public void showSetWallDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.set_wall_title)
                .content(R.string.set_wall_content)
                .positiveText(R.string.set_it)
                .neutralText(R.string.crop_wall)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        showSettingWallDialog(false);
                        Picasso.with(DetailedWallpaperActivity.this)
                                .load(wall)
                                .into(wallTarget);

                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        Picasso.with(DetailedWallpaperActivity.this)
                                .load(wall)
                                .into(wallCropTarget);
                    }
                }).show();
    }

    public void showSettingWallDialog(boolean indeterminate) {
        if (indeterminate) {
            new MaterialDialog.Builder(this)
                    .title(R.string.setting_wall_title)
                    .content(R.string.please_wait)
                    .progress(true, 0)
                    .show();
        } else {
            new MaterialDialog.Builder(this)
                    .title(R.string.setting_wall_title)
                    .content(R.string.please_wait)
                    .progress(false, 60)
                    .showListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            final MaterialDialog dialog = (MaterialDialog) dialogInterface;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (dialog.getCurrentProgress() != dialog.getMaxProgress()) {
                                        if (dialog.isCancelled())
                                            break;
                                        try {
                                            Thread.sleep(50);
                                        } catch (InterruptedException e) {
                                            break;
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.incrementProgress(1);
                                            }
                                        });
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.setTitle(getString(R.string.done));
                                            dialog.setContent(getString(R.string.set_as_wall_done));
                                            dialog.setActionButton(DialogAction.NEGATIVE, R.string.close);
                                        }
                                    });
                                }
                            }).start();
                        }
                    }).show();
        }
    }

    private void showNoPicDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.error)
                .content(R.string.wall_error)
                .positiveText(android.R.string.ok)
                .show();
    }

    public Uri getLocalBitmapUri(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp;
        if (drawable instanceof BitmapDrawable)
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        else
            return null;
        Uri bmpUri = null;
        try {
            File file = new File(saveWallLocation, picName + convertWallName(wall) + ".png");
            file.getParentFile().mkdirs();
            file.delete();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}
