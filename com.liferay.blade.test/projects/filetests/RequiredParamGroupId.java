public class RequiredParamGroupId {
	public void testCall(){

		//AssetTag
		String str = "test";
		String[] strs = new String[]{"test1","test2"};
		ServiceContext sc = new ServiceContext();
		AssetTagServiceUtil.addTag(str,strs,sc);

		long larg = 1L;
		AssetTagLocalServiceUtil.addTag(larg,strs,sc);

		long parentCategoryId;
		Map<Locale, String> titleMap;
		Map<Locale, String> descriptionMap;
		long vocabularyId;
		String[] categoryProperties;
		AssetCategoryServiceUtil.addCategory(parentCategoryId,titleMap,descriptionMap,vocabularyId,categoryProperties,sc);
		AssetCategoryServiceUtil.addCategory(str,vocabularyId,sc);

		//AssetCategory

		long userId;
		//long parentCategoryId;
		// Map<Locale, String> titleMap;
		// Map<Locale, String> descriptionMap,
		//long vocabularyId,
		//java.lang.String[] categoryProperties,
		//com.liferay.portal.service.ServiceContext serviceContext)
		AssetCategoryLocalServiceUtil.addCategory(userId,parentCategoryId,titleMap,descriptionMap,vocabularyId,categoryProperties,sc);

		// long userId;
		// String title;
		// long vocabularyId;
		//ServiceContext serviceContext;
		AssetCategoryLocalServiceUtil.addCategory(userId,str,vocabularyId,sc);

		//AssetVocabulary

		// java.util.Map<java.util.Locale, java.lang.String> titleMap;
		// java.util.Map<java.util.Locale, java.lang.String> descriptionMap,
		// java.lang.String settings,
		// com.liferay.portal.service.ServiceContext serviceContext
		AssetVocabularyServiceUtil.addVocabulary(titleMap,descriptionMap,str,sc);

		//java.lang.String title,
		//java.util.Map<java.util.Locale, java.lang.String> titleMap,
		//java.util.Map<java.util.Locale, java.lang.String> descriptionMap,
		//java.lang.String settings,
		//com.liferay.portal.service.ServiceContext serviceContext
		AssetVocabularyServiceUtil.addVocabulary(str,titleMap,descriptionMap,str,sc);
		//java.lang.String title,
		//com.liferay.portal.service.ServiceContext serviceContext
		AssetVocabularyServiceUtil.addVocabulary(str,sc);

		//long userId,
		//java.util.Map<java.util.Locale, java.lang.String> titleMap,
		//java.util.Map<java.util.Locale, java.lang.String> descriptionMap,
		//java.lang.String settings,
		//com.liferay.portal.service.ServiceContext serviceContext
		AssetVocabularyLocalServiceUtil.addVocabulary(userId,titleMap,descriptionMap,str,sc);
		//long userId,
		//java.lang.String title,
		//java.util.Map<java.util.Locale, java.lang.String> titleMap,
		//java.util.Map<java.util.Locale, java.lang.String> descriptionMap,
		//java.lang.String settings,
		//com.liferay.portal.service.ServiceContext serviceContext
		AssetVocabularyLocalServiceUtil.addVocabulary(userId,str,titleMap,descriptionMap,str,sc);
		//long userId,
		//java.lang.String title,
		//com.liferay.portal.service.ServiceContext serviceContext
		AssetVocabularyLocalServiceUtil.addVocabulary(userId,str,sc);

		//JournalFolder

		//long folderId,
		//long parentFolderId,
		//java.lang.String name,
		//java.lang.String description,
		boolean mergeWithParentFolder,
		//com.liferay.portal.service.ServiceContext serviceContext
		JournalFolderServiceUtil.updateFolder(userId,userId,str,str,mergeWithParentFolder,sc);
		//long userId,
		//long folderId,
		//long parentFolderId,
		//java.lang.String name,
		//java.lang.String description,
		//boolean mergeWithParentFolder,
		//com.liferay.portal.service.ServiceContext serviceContext)
		JournalFolderLocalServiceUtil.updateFolder(userId,userId,userId,str,str,mergeWithParentFolder,sc);
	}
}
