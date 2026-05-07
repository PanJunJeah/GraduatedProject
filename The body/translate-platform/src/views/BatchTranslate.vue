<template>
  <div class="batch-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h2>批量翻译</h2>
          <div class="header-actions">
            <el-button type="primary" @click="$router.push('/translate')">
              <el-icon><Back /></el-icon> 返回单条翻译
            </el-button>
          </div>
        </div>
      </el-header>

      <el-main>
        <el-card>
          <div class="language-selector">
            <div class="language-group">
              <span class="label">源语言</span>
              <el-select v-model="sourceLang">
                <el-option label="中文" value="zh" />
                <el-option label="英文" value="en" />
                <el-option label="日文" value="ja" />
                <el-option label="韩文" value="ko" />
                <el-option label="法文" value="fr" />
                <el-option label="德文" value="de" />
                <el-option label="西班牙文" value="es" />
              </el-select>
            </div>

            <div class="language-group">
              <span class="label">目标语言</span>
              <el-select v-model="targetLang">
                <el-option label="中文" value="zh" />
                <el-option label="英文" value="en" />
                <el-option label="日文" value="ja" />
                <el-option label="韩文" value="ko" />
                <el-option label="法文" value="fr" />
                <el-option label="德文" value="de" />
                <el-option label="西班牙文" value="es" />
              </el-select>
            </div>
          </div>

          <el-input
            v-model="batchText"
            type="textarea"
            :rows="10"
            placeholder="请输入要翻译的文本，每行一条..."
            resize="none"
          />

          <div class="action-area">
            <el-button type="primary" size="large" @click="handleBatchTranslate" :loading="isTranslating">
              <el-icon v-if="!isTranslating"><Translate /></el-icon>
              {{ isTranslating ? '翻译中...' : '开始批量翻译' }}
            </el-button>
            <el-button @click="handleClear" size="large">清空</el-button>
          </div>

          <div v-if="results.length > 0" class="results-section">
            <h3>翻译结果</h3>
            <el-table :data="results" stripe style="width: 100%">
              <el-table-column prop="index" label="序号" width="80" />
              <el-table-column prop="source" label="原文" show-overflow-tooltip />
              <el-table-column prop="translation" label="译文" show-overflow-tooltip />
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button type="text" @click="handleCopy(row.translation)">复制</el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="export-area">
              <el-button @click="handleExport">导出结果</el-button>
            </div>
          </div>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { batchAPI } from '../api';

const batchText = ref('');
const sourceLang = ref('zh');
const targetLang = ref('en');
const isTranslating = ref(false);
const results = ref([]);

const handleBatchTranslate = async () => {
  if (!batchText.value.trim()) {
    ElMessage.warning('请输入要翻译的文本');
    return;
  }

  const texts = batchText.value.split('\n').filter(t => t.trim());
  if (texts.length === 0) {
    ElMessage.warning('请输入有效的文本');
    return;
  }

  isTranslating.value = true;
  try {
    const response = await batchAPI.batchTranslate({
      texts: texts,
      sourceLang: sourceLang.value,
      targetLang: targetLang.value,
    });

    if (response.success) {
      results.value = response.data.translations.map((translation, index) => ({
        index: index + 1,
        source: texts[index],
        translation: translation,
      }));
      ElMessage.success(`翻译完成，成功 ${response.data.successCount}/${response.data.totalCount}`);
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '批量翻译失败');
  } finally {
    isTranslating.value = false;
  }
};

const handleClear = () => {
  batchText.value = '';
  results.value = [];
};

const handleCopy = async (text) => {
  try {
    await navigator.clipboard.writeText(text);
    ElMessage.success('已复制到剪贴板');
  } catch {
    ElMessage.error('复制失败');
  }
};

const handleExport = () => {
  const content = results.value
    .map(r => `${r.index}. ${r.source} => ${r.translation}`)
    .join('\n');

  const blob = new Blob([content], { type: 'text/plain' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `batch_translation_${Date.now()}.txt`;
  a.click();
  URL.revokeObjectURL(url);

  ElMessage.success('导出成功');
};
</script>

<style scoped>
.batch-page {
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

.language-selector {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 20px;
}

.language-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.language-group .label {
  font-weight: bold;
  color: #666;
}

.action-area {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

.results-section {
  margin-top: 30px;
}

.results-section h3 {
  margin-bottom: 15px;
  color: #333;
}

.export-area {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
