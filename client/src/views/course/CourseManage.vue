<template>
  <div class="course-manage-container">
    <el-page-header @back="goBack" content="课程内容管理" class="page-header"></el-page-header>
    
    <el-tabs v-model="activeTab">
      <!-- 章节内容管理 -->
      <el-tab-pane label="章节内容" name="chapters">
        <el-row :gutter="20">
          <!-- 左侧：课程目录树 -->
          <el-col :span="8">
            <el-card class="box-card">
              <template #header>
                <div class="card-header">
                  <span>课程目录</span>
                  <el-button type="primary" :icon="Plus" @click="addChapter">添加章</el-button>
                </div>
              </template>
              <el-tree
                :data="chapterTree"
                :props="treeProps"
                node-key="id"
                :expand-on-click-node="false"
                :indent="24"
                draggable
                :allow-drop="allowDrop"
                @node-drop="handleNodeDrop"
              >
                <template #default="{ node, data }">
                  <span class="custom-tree-node">
                    <span>{{ getNumberedLabel(node) }}</span>
                    <span>
                      <el-button
                        type="text"
                        size="small"
                        v-if="data.type === 1"
                        @click="() => addSection(data)">
                        添加节
                      </el-button>
                      <el-button
                        type="text"
                        size="small"
                        @click="() => editNode(node, data)">
                        编辑
                      </el-button>
                      <el-button
                        type="text"
                        size="small"
                        style="color: #F56C6C;"
                        @click="() => removeNode(node, data)">
                        删除
                      </el-button>
                    </span>
                  </span>
                </template>
              </el-tree>
            </el-card>
          </el-col>
          
          <!-- 右侧：内容编辑区 -->
          <el-col :span="16">
            <el-card class="box-card">
              <template #header>
                <div class="card-header">
                  <span>{{ Object.keys(currentNode).length > 0 ? (currentNode.id ? '编辑内容' : '添加内容') : '请从左侧选择节点' }}</span>
                </div>
              </template>
              <div v-if="Object.keys(currentNode).length > 0">
                <el-form :model="currentNode" label-width="80px">
                    <el-form-item label="标题">
                        <el-input v-model="currentNode.title" />
                    </el-form-item>
                    <!-- 如果是视频节，显示上传组件 -->
                    <el-form-item v-if="currentNode.type === 2" label="视频">
                        <el-upload
                          class="video-uploader"
                          :http-request="uploadVideoHttpRequest"
                          :show-file-list="false"
                          :disabled="!currentNode.id"
                        >
                          <el-button size="small" type="primary" :disabled="!currentNode.id">点击上传</el-button>
                          <template #tip>
                            <div class="el-upload__tip" v-if="!currentNode.id">
                              请先保存当前课时，然后再上传视频。
                            </div>
                            <div v-if="isUploading" class="upload-progress">
                              <el-progress 
                                :percentage="uploadProgress"
                                :format="() => `${formatFileSize(uploadedSize)}/${formatFileSize(totalSize)} (${uploadProgress}%)`"
                                status="success"
                              />
                            </div>
                          </template>
                        </el-upload>
                        <br/>
                        <div v-if="currentNode.url" class="video-preview">
                            <div class="video-url">
                                视频链接: <el-link :href="currentNode.url" type="primary" target="_blank">{{ currentNode.url }}</el-link>
                            </div>
                            <div class="video-container">
                                <video 
                                    :src="currentNode.url" 
                                    controls
                                    class="video-player"
                                ></video>
                            </div>
                        </div>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="saveNode">保存</el-button>
                        <el-button @click="currentNode = {}">取消</el-button>
                    </el-form-item>
                </el-form>
              </div>
              <el-empty v-else description="请选择一个节点进行操作"></el-empty>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 作业管理 -->
      <el-tab-pane label="作业管理" name="homework">
        <div class="tab-header">
            <h3>作业列表</h3>
            <el-button type="primary" :icon="Plus" @click="handleCreateHomework">发布新作业</el-button>
        </div>
        <el-table :data="homeworkList" style="width: 100%" v-loading="homeworkLoading">
            <el-table-column prop="title" label="作业标题" />

            <el-table-column prop="endTime" label="结束时间" />
            <el-table-column label="操作" width="240">
                <template #default="scope">
                    <el-button-group>
                        <el-button size="small" type="success" @click="handleGradeHomework(scope.row)">批改</el-button>
                        <el-button size="small" type="primary" @click="handleEditHomework(scope.row)">编辑</el-button>
                        <el-button size="small" type="danger" @click="handleDeleteHomework(scope.row)">删除</el-button>
                    </el-button-group>
                </template>
            </el-table-column>
        </el-table>
        <el-empty v-if="homeworkList.length === 0" description="暂无作业，快去新建一个吧！"></el-empty>
      </el-tab-pane>

      <!-- 学生管理 -->
      <el-tab-pane label="学生管理" name="students">
        <div class="tab-header">
          <h3>学生列表</h3>
          <el-button type="primary" :icon="Plus" @click="handleInviteStudent">邀请学生</el-button>
        </div>
        
        <el-table :data="studentList" style="width: 100%" v-loading="studentsLoading">
          <el-table-column prop="username" label="学号" />
          <el-table-column prop="realName" label="姓名" />
          <el-table-column prop="email" label="邮箱" />
          <el-table-column prop="joinTime" label="加入时间" />
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button 
                size="small" 
                type="danger" 
                @click="handleRemoveStudent(scope.row)"
              >移出课程</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 邀请学生对话框 -->
        <el-dialog
          v-model="inviteDialogVisible"
          title="邀请学生"
          width="30%"
        >
          <el-form :model="inviteForm" label-width="80px">
            <el-form-item label="学号">
              <el-input v-model="inviteForm.username" placeholder="请输入学生学号" />
            </el-form-item>
          </el-form>
          <template #footer>
            <span class="dialog-footer">
              <el-button @click="inviteDialogVisible = false">取消</el-button>
              <el-button type="primary" @click="confirmInviteStudent">确认</el-button>
            </span>
          </template>
        </el-dialog>
      </el-tab-pane>
    </el-tabs>

    <!-- 聊天组件 -->
    <ChatContainer v-if="props.courseId" :course-id="props.courseId" />

  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { useUserStore } from '../../stores/user';
import ChatContainer from '@/components/common/ChatContainer.vue';
import { getCourseChapterTree, saveOrUpdateChapter, deleteChapter, getUploadToken, reorderChapters } from '@/api/courseChapter';
import { getHomeworkList, saveHomework, deleteHomework } from '@/api/homework';
import { getCourseStudents, inviteStudent, removeStudent } from '@/api/course';
import axios from 'axios';

const userStore = useUserStore()
// 假定API文件已更新
// import { saveHomework as apiSaveHomework } from '@/api/homework'; // 暂时注释掉

const getNumberedLabel = (node) => {
  // 层级为1，是章
  if (node.level === 1) {
    const chapterIndex = node.parent.childNodes.indexOf(node) + 1;
    return `${chapterIndex} ${node.data.title}`;
  }
  // 层级为2，可能是节或作业
  if (node.level === 2) {
    // 通过ID在原始数据中查找父节点（章）的索引，这比通过对象引用查找更可靠
    const chapterIndex = chapterTree.value.findIndex(c => c.id === node.parent.data.id) + 1;
    const sectionIndex = node.parent.childNodes.indexOf(node) + 1;
    
    const prefix = `${chapterIndex}.${sectionIndex}`;
    const typeLabel = node.data.type === 3 ? '作业' : ''; // 添加作业标识
    
    return `${prefix} ${typeLabel} ${node.data.title}`;
  }
  return node.data.title;
};

const props = defineProps({
  courseId: {
    type: [String, Number],
    required: true
  }
});

// --- 新增：七牛云域名配置 ---
// TODO: 请将此处替换为你自己在 application.properties 中配置的七牛云域名
const QINIU_DOMAIN = 'http://sxh8oib6z.hb-bkt.clouddn.com';

const router = useRouter();
const chapterTree = ref([]);
const currentNode = ref({}); // 当前正在编辑的节点
const treeProps = {
  children: 'children',
  label: 'title',
};

const activeTab = ref('chapters');
const homeworkList = ref([]);
const homeworkLoading = ref(false);

// --- 七牛云上传相关 ---
const qiniuData = ref({
  uploadUrl: 'http://upload-z1.qiniup.com/' // 默认为华南z2区，请根据你的存储区域修改
});

// 添加用于格式化文件大小的函数
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 添加上传进度相关的响应式变量
const uploadProgress = ref(0);
const isUploading = ref(false);
const uploadedSize = ref(0);
const totalSize = ref(0);

// 学生管理相关的响应式变量
const studentList = ref([]);
const studentsLoading = ref(false);
const inviteDialogVisible = ref(false);
const inviteForm = ref({
  username: ''
});

onMounted(async () => {
  await fetchChapterTree();
  await fetchHomeworkList(); // 组件挂载时也获取一次作业列表
  await fetchUploadToken();
  await fetchStudentList();
});

// 监听标签页切换
watch(activeTab, (newTab) => {
    if (newTab === 'homework') {
        fetchHomeworkList();
    } else if (newTab === 'students') {
        fetchStudentList();
    }
});

const fetchHomeworkList = async () => {
    try {
        const res = await getHomeworkList(props.courseId);
        homeworkList.value = res;
    } catch (error) {
        ElMessage.error('获取作业列表失败');
        console.error(error);
    }
};

const fetchChapterTree = async () => {
  try {
    const res = await getCourseChapterTree(props.courseId);
    // 后端已经返回树形结构，直接使用即可
    chapterTree.value = res;
  } catch (error) {
    ElMessage.error('获取课程目录失败');
  }
};

const fetchUploadToken = async () => {
    try {
        if(userStore.isTeacher) {
        // This is now handled within the upload request, so this pre-fetching can be removed or simplified.
        // For now, let's keep it but it's not strictly necessary for the upload itself.
        const r = await getUploadToken();
        qiniuData.value.token = r; // This might be stale, real token is fetched on-demand.
      }
    } catch(e) {
      console.error(e);
      ElMessage.error("获取上传凭证失败，请检查后端服务和七牛云配置");
    }
};

const goBack = () => router.push({ name: 'home' });

const addChapter = () => {
  currentNode.value = {
    courseId: props.courseId,
    parentId: 0,
    type: 1, // 1表示章
    title: '',
    orderNum: chapterTree.value.length + 1 // 默认排序号为最后
  };
  ElMessage.info('请在右侧表单中填写新章节的信息');
};

const addSection = (data) => {
    currentNode.value = {
        courseId: props.courseId,
        parentId: data.id,
        type: 2, // 2表示节
        title: '',
        url: '',
        orderNum: data.children ? data.children.length + 1 : 1
    };
    ElMessage.info('请在右侧表单中填写新课时的信息');
};

const editNode = (node, data) => {
    currentNode.value = { ...data }; // 浅拷贝一份数据进行编辑
};

const removeNode = async (node, data) => {
    let confirmMessage = '';
    if (data.type === 1) {
        confirmMessage = '此操作将永久删除该章节及其下所有课时和作业, 是否继续?';
    } else if (data.type === 2) {
        confirmMessage = '此操作将永久删除该课时, 是否继续?';
    } else {
        confirmMessage = '此操作将永久删除该作业, 是否继续?';
    }
        
    try {
        await ElMessageBox.confirm(confirmMessage, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        });
        
        await deleteChapter(data.id);
        ElMessage.success('删除成功!');
        
        // If the deleted node is the one being edited, clear the form
        if (currentNode.value.id === data.id) {
            currentNode.value = {};
        }
        await fetchChapterTree(); // Refresh tree
    } catch (error) {
        if (error === 'cancel') {
            ElMessage.info('已取消删除');
        } else {
            console.error(error);
            ElMessage.error('删除失败');
        }
    }
};

const saveNode = async () => {
    if (!currentNode.value.title || !currentNode.value.title.trim()) {
        ElMessage.warning('请输入标题');
        return;
    }
    try {
        const savedData = await saveOrUpdateChapter(currentNode.value);
        ElMessage.success('保存成功!');
        await fetchChapterTree(); // 刷新树
        // 更新当前节点为保存后的数据（特别是为了获取新ID）
        currentNode.value = { ...savedData };
    } catch (error) {
        ElMessage.error('保存失败');
        console.error(error);
    }
};

/**
 * 拖拽时判断目标节点是否可以放置
 * @param {object} draggingNode - 被拖拽的节点
 * @param {object} dropNode - 目标节点
 * @param {string} type - 放置类型 ('prev', 'inner', 'next')
 */
const allowDrop = (draggingNode, dropNode, type) => {
    // 不允许将"章"拖拽到"节"的内部
    if (dropNode.data.type === 2 && type === 'inner') {
        return false;
    }
    // 不允许跨级拖拽（例如，把一个"节"拖到根级别）
    if (draggingNode.parent.id !== dropNode.parent.id) {
        return false;
    }
    return true;
};

/**
 * 拖拽成功完成时触发
 */
const handleNodeDrop = async (draggingNode, dropNode, dropType, ev) => {
    try {
        ElMessage.info('正在保存新顺序...');
        const parentNode = dropNode.parent;
        const children = parentNode.childNodes;

        const reorderList = children.map((node, index) => ({
            id: node.data.id,
            orderNum: index + 1
        }));
        
        await reorderChapters(reorderList);
        ElMessage.success('顺序更新成功!');
        await fetchChapterTree(); // 刷新以确认顺序
    } catch (error) {
        ElMessage.error('顺序更新失败');
        // 可以在此处重新获取数据以恢复到之前的状态
        await fetchChapterTree();
    }
};

// --- 视频上传钩子 ---
const uploadVideoHttpRequest = async ({ file }) => {
    const isMp4 = file.type === 'video/mp4';
    if (!isMp4) {
        ElMessage.error('请上传 MP4 格式的视频文件！');
        return;
    }

    const isLt500M = file.size / 1024 / 1024 < 500;

    if (!isLt500M) {
        ElMessage.error('上传视频大小不能超过 500MB!');
        return;
    }

    if (!currentNode.value.id) {
        ElMessage.error('请先保存课时信息，然后再上传视频！');
        return;
    }

    try {
        const token = await getUploadToken();
        if (!token) {
            ElMessage.error('未能获取上传凭证，无法上传！');
            return;
        }

        const fileExt = file.name.split('.').pop();
        const key = `videos/${props.courseId}/${currentNode.value.id}_${Date.now()}${fileExt ? '.' + fileExt : ''}`;
        
        const formData = new FormData();
        formData.append('token', token);
        formData.append('key', key);
        formData.append('file', file);
        
        isUploading.value = true;
        uploadProgress.value = 0;

        const res = await axios.post(qiniuData.value.uploadUrl, formData, {
            onUploadProgress: (progressEvent) => {
                totalSize.value = progressEvent.total;
                uploadedSize.value = progressEvent.loaded;
                uploadProgress.value = Math.round((progressEvent.loaded * 100) / progressEvent.total);
            }
        });

        isUploading.value = false;
        const fileUrl = `${QINIU_DOMAIN}/${res.data.key}`;
        currentNode.value.url = fileUrl;
        ElMessage.success('视频上传成功');
        
        setTimeout(() => {
            uploadProgress.value = 0;
        }, 3000);

    } catch (err) {
        isUploading.value = false;
        uploadProgress.value = 0;
        ElMessage.error('视频上传失败');
        console.error(err);
    }
};

const handleGradeHomework = (homework) => {
    router.push({
        name: 'HomeworkGrading',
        params: {
            courseId: props.courseId,
            homeworkId: homework.id
        }
    });
};

const handleCreateHomework = async () => {
    const newHomework = {
        courseId: props.courseId,
        title: `新作业 - ${new Date().toLocaleString()}`,
        description: '请填写作业描述...',
        questions: [],
        startTime: new Date(),
        endTime: new Date(new Date().getTime() + 7 * 24 * 60 * 60 * 1000)
    };

    try {
        const savedHomework = await saveHomework(newHomework);
        ElMessage.success('作业创建成功，正在跳转到编辑器...');
        router.push({ 
            name: 'HomeworkEditor', 
            params: { courseId: props.courseId, homeworkId: savedHomework.id }
        });
    } catch (error) {
        ElMessage.error('创建作业失败');
        console.error(error);
    }
};

const handleEditHomework = (homework) => {
    router.push(`/course/${props.courseId}/homework/edit/${homework.id}`);
};

const handleDeleteHomework = (row) => {
    ElMessageBox.confirm(
        `确定要删除作业《${row.title}》吗？这将同时删除所有学生的提交记录，此操作不可恢复。`,
        '危险操作确认',
        {
            confirmButtonText: '确认删除',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        try {
            await deleteHomework(row.id);
            ElMessage.success('作业删除成功');
            fetchHomeworkList(); // Refresh the list
        } catch (error) {
            ElMessage.error('删除失败，请稍后重试');
            console.error(error);
        }
    }).catch(() => {
        // User canceled
    });
};

// 获取学生列表
const fetchStudentList = async () => {
    try {
        studentsLoading.value = true;
        const res = await getCourseStudents(props.courseId);
        studentList.value = res;
    } catch (error) {
        ElMessage.error('获取学生列表失败');
        console.error(error);
    } finally {
        studentsLoading.value = false;
    }
};

// 打开邀请学生对话框
const handleInviteStudent = () => {
    inviteForm.value.username = '';
    inviteDialogVisible.value = true;
};

// 确认邀请学生
const confirmInviteStudent = async () => {
    if (!inviteForm.value.username) {
        ElMessage.warning('请输入学生学号');
        return;
    }
    
    try {
        await inviteStudent(props.courseId, inviteForm.value.username);
        ElMessage.success('邀请成功');
        inviteDialogVisible.value = false;
        fetchStudentList(); // 刷新学生列表
    } catch (error) {
        ElMessage.error(error.message || '邀请失败');
        console.error(error);
    }
};

// 移出学生
const handleRemoveStudent = async (student) => {
    try {
        await ElMessageBox.confirm(
            `确定要将学生 ${student.realName}(${student.username}) 移出课程吗？`,
            '提示',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }
        );
        
        await removeStudent(props.courseId, student.id);
        ElMessage.success('已移出学生');
        fetchStudentList(); // 刷新学生列表
    } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('操作失败');
          console.error(error);
        }
    }
};

</script>

<style scoped>
.course-manage-container {
  padding: 0px 20px 20px;
  --primary-color: #6996f8;
  --primary-light: #ebf2ff;
  --primary-lighter: #f5f8ff;
  --border-color: #e2e8f0;
  --text-primary: #1e293b;
  --text-secondary: #64748b;
  --background-color: #f8fafc;
  --card-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  --card-shadow-hover: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  min-height: 100vh;
  position: relative; /* 为FAB按钮定位 */
}

.page-header {
  margin-bottom: 10px;
  padding-bottom: 15px;
  position: relative;
}

.page-header:after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 60px;
  height: 3px;
  background: var(--primary-color);
  border-radius: 3px;
}

.box-card {
  border-radius: 12px;
  border: none;
  box-shadow: var(--card-shadow);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  overflow: hidden;
  position: relative;
}

.box-card:hover {
  box-shadow: var(--card-shadow-hover);
  transform: translateY(-2px);
}

.box-card:before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--primary-color);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-bottom: 1px solid var(--border-color);
}

.el-tree {
  --el-tree-node-indent: 24px;
  padding: 0 16px;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding: 6px 8px;
  transition: all 0.3s ease;
}

.custom-tree-node:hover {
  background-color: var(--primary-lighter);
  border-radius: 4px;
}

.el-form {
  padding: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-button {
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.el-button--primary {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
  box-shadow: 0 4px 6px -1px rgba(37, 99, 235, 0.3), 0 2px 4px -1px rgba(37, 99, 235, 0.1);
}

.el-button--primary:hover {
  background-color: #1d4ed8;
  border-color: #1d4ed8;
  box-shadow: 0 10px 15px -3px rgba(37, 99, 235, 0.3), 0 4px 6px -2px rgba(37, 99, 235, 0.1);
  transform: translateY(-1px);
}

.video-preview {
  margin-top: 20px;
}

.video-container {
  position: relative;
  width: 100%;
  max-width: 800px;
  background: #000;
  padding-top: 56.25%;
  border-radius: 8px;
  overflow: hidden;
}

.video-player {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.upload-progress {
  margin-top: 10px;
  width: 100%;
  max-width: 400px;
}

.video-uploader {
  width: 100%;
  max-width: 400px;
}

.el-upload__tip {
  width: 100%;
  max-width: 400px;
  color: var(--text-secondary);
  font-size: 13px;
}

.el-table {
  margin-top: 20px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: var(--card-shadow);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .course-manage-container {
    padding: 20px 15px;
  }
  
  .box-card {
    border-radius: 0;
    box-shadow: none;
  }
  
  .el-col {
    width: 100%;
    margin-bottom: 20px;
  }
}

.tab-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>