package com.liferay.test;

import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;


public class DLAppServiceUtilTest {

    public void moveFileEntryFromTrash() {
        DLAppServiceUtil.moveFileEntryFromTrash(null);
    }

    public void moveFileEntryToTrash() {
        DLAppServiceUtil.moveFileEntryToTrash(null);
    }

    public void moveFileShortcutFromTrash() {
        DLAppServiceUtil.moveFileShortcutFromTrash(null);
    }

    public void moveFileShortcutToTrash() {
        DLAppServiceUtil.moveFileShortcutToTrash(null);
    }

    public void moveFolderFromTrash() {
        DLAppServiceUtil.moveFolderFromTrash(null);
    }

    public void moveFolderToTrash() {
        DLAppServiceUtil.moveFolderToTrash(null);
    }

    public void restoreFileEntryFromTrash() {
        DLAppServiceUtil.restoreFileEntryFromTrash(null);
    }

    public void restoreFileShortcutFromTrash() {
        DLAppServiceUtil.restoreFileShortcutFromTrash(null);
    }

    public void restoreFolderFromTrash() {
        DLAppServiceUtil.restoreFolderFromTrash(null);
    }

    public void moveFileEntryToTrash() {
        DLAppLocalServiceUtil.moveFileEntryToTrash(null);
    }

    public void restoreFileEntryFromTrash() {
        DLAppLocalServiceUtil.restoreFileEntryFromTrash(null);
    }

}
