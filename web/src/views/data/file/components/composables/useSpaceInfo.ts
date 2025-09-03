import { ref, watch, computed } from 'vue';
import { pubSpace, space } from '@/api/storage';
import { useI18n } from 'vue-i18n';
import type { SpaceInfoColumnType } from '../../components/type';

interface Props {
  id: string;
  type?: 'space' | 'file' | 'directory';
  pubapi?: boolean;
}

/**
 * Composable for managing space information display
 * Handles loading space/file data and configuring information grid columns
 *
 * @param props - Component props containing ID, type, and API flags
 * @returns Object containing reactive state and methods for space information
 */
export function useSpaceInfo (props: Props) {
  // Reactive state
  const dataSource = ref<Record<string, any>>({});
  const loading = ref(false);

  const { t } = useI18n();

  /**
   * Computed column configuration for information grid
   */
  const infoColumns = computed<SpaceInfoColumnType[][]>(() => [
    [
      { dataIndex: 'name', label: t('fileSpace.spaceDetail.columns.name') },
      { dataIndex: 'id', label: t('fileSpace.spaceDetail.columns.id') },
      props.type !== 'space' && {
        dataIndex: 'summary',
        label: t('fileSpace.spaceDetail.columns.actualSize'),
        customRender: ({ text }: { text: any }) => text?.usedSize
      },
      props.type === 'space' && {
        dataIndex: 'quotaSize',
        label: t('fileSpace.spaceDetail.columns.quota'),
        customRender: ({ text }: { text: any }) => text ? text?.value + text?.unit?.message : '--'
      },
      {
        dataIndex: 'type',
        label: t('fileSpace.spaceDetail.columns.format'),
        customRender: ({ text }: { text: any }) => text?.message || '空间'
      },
      { dataIndex: 'createdByName', label: t('fileSpace.spaceDetail.columns.createdBy') },
      { dataIndex: 'createdDate', label: t('fileSpace.spaceDetail.columns.createdDate') },
      { dataIndex: 'lastModifiedDate', label: t('fileSpace.spaceDetail.columns.lastModifiedDate') },
      props.type === 'space' && { dataIndex: 'remark', label: t('fileSpace.spaceDetail.columns.remark') },
      props.type === 'file' && { dataIndex: 'mockFunc', label: t('fileSpace.spaceDetail.columns.mockFunc') }
    ].filter(Boolean) as SpaceInfoColumnType[]
  ]);

  /**
   * Load information data based on ID and type
   */
  const loadInfo = async (): Promise<void> => {
    loading.value = true;

    try {
      const [error, res] = await (
        props.type === 'space'
          ? space.getSpaceDetail(props.id)
          : props.pubapi
            ? pubSpace.getFileInfo(props.id)
            : space.getFileDetail(props.id)
      );

      console.log('API response:', { error, res });

      if (error) {
        console.error('API error:', error);
        return;
      }

      dataSource.value = res.data;
      console.log('dataSource updated:', dataSource.value);
    } finally {
      loading.value = false;
    }
  };

  /**
   * Initialize composable - watch for ID changes
   */
  const init = (): void => {
    watch(() => props.id, newValue => {
      if (!newValue) {
        return;
      }
      loadInfo();
    }, {
      immediate: true
    });
  };

  return {
    // State
    dataSource,
    loading,
    infoColumns,

    // Methods
    init
  };
}
