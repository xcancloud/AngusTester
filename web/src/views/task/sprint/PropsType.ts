export type SprintInfo = {
    id: string;
    projectId: string;
    projectName: string;
    name: string;
    status: {
        value: string;
        message: string;
    };
    attachments: {
        id: string;
        name: string;
        url: string;
    }[];
    description: string;
    authFlag: boolean;
    startDate: string;
    deadlineDate: string;
    ownerId: string;
    ownerName: string;
    ownerAvatar: string;
    acceptanceCriteria: string;
    otherInformation: string;
    autoUpdateResultByExec: boolean;
    evalWorkloadMethod: {
        value: string;
        message: string;
    };
    tenantId: string;
    tenantName: string;
    createdBy: string;
    createdByName: string;
    createdDate: string;
    lastModifiedBy: string;
    lastModifiedByName: string;
    lastModifiedDate: string;
    taskNum: string;
    taskPrefix: string;
    progress: {
        total: string;
        completed: string;
        completedRate: string;
    };
    members: {
        id: string;
        username: string;
        fullname: string;
        mobile: string;
        email: string;
        avatar: string;
    }[];
    showMembers: {
        id: string;
        username: string;
        fullname: string;
        mobile: string;
        email: string;
        avatar: string;
    }[];
    meetings?: {
        type: {
            value: string;
            message: string
        };
        date: string;
        time: string;
        location: string;
        moderator: {id:string;fullname:string;};
        participants: {id:string;fullname:string;}[];
        content: string;
    }[];
}

export type IPane = {
    _id: string;// pane唯一标识
    name: string; // pane的tab文案
    value: string;// pane内部的组件标识
    closable?: boolean;// 是否允许关闭，true - 允许关闭，false - 禁止关闭
    forceRender?: boolean;// 被隐藏时是否渲染 DOM 结构，可选
    icon?: string;// tab文案前面的icon，可选
    active?: boolean; // 是否选中，添加不用设置，缓存时用于记录上次激活的tab pane，可选

    // 组件需要的属性
    data?: { [key: string]: any; };
};
