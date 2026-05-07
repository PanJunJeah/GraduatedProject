<template>
  <div class="history-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h2>翻译历史</h2>
          <div class="header-actions">
            <el-button type="primary" @click="$router.push('/translate')">
              <el-icon><Back /></el-icon> 返回翻译
            </el-button>
          </div>
        </div>
      </el-header>

      <el-main>
        <el-card>
          <el-table :data="history" v-loading="isLoading" stripe style="width: 100%">
            <el-table-column prop="createdAt" label="时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column prop="sourceLang" label="源语言" width="100">
              <template #default="{ row }">
                {{ getLangName(row.sourceLang) }}
              </template>
            </el-table-column>
            <el-table-column prop="targetLang" label="目标语言" width="100">
              <template #default="{ row }">
                {{ getLangName(row.targetLang) }}
              </template>
            </el-table-column>
            <el-table-column prop="sourceText" label="原文" show-overflow-tooltip />
            <el-table-column prop="translatedText" label="译文" show-overflow-tooltip />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="text" @click="handleCopy(row)">复制</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-empty v-if="!isLoading && history.length === 0" description="暂无翻译历史" />
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useTranslationStore } from '../stores/translation';

const translationStore = useTranslationStore();
const history = ref([]);
const isLoading = ref(false);

const langMap = {
  zh: '中文',
  en: '英文',
  ja: '日文',
  ko: '韩文',
  fr: '法文',
  de: '德文',
  es: '西班牙文',
};

const getLangName = (code) => langMap[code] || code;

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN');
};

const handleCopy = async (row) => {
  try {
    await navigator.clipboard.writeText(row.translatedText);
    ElMessage.success('已复制到剪贴板');
  } catch {
    ElMessage.error('复制失败');
  }
};

onMounted(async () => {
  isLoading.value = true;
  await translationStore.loadHistory();
  history.value = translationStore.history;
  isLoading.value = false;
});
</script>

<style scoped>
.history-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.el-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.header-content h2 {
  margin: 0;
  color: #333;
}

.el-main {
  padding: 20px;
}
</style>
