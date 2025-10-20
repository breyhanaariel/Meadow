
// firebase/firestore.rules

rules_version = '2';
service cloud.firestore {
    match /databases/{database}/documents {

        // Projects
        match /projects/{projectId} {
            allow read, write: if request.auth != null
            && request.auth.token.email in get(/databases/$(database)/documents/projects/$(projectId)).data.collaborators;
        }

        // Collaborators
        match /collaborators/{docId} {
            allow read: if request.auth != null;
            allow write: if request.auth != null
                && request.auth.token.email == resource.data.email;
        }

        // Settings
        match /settings/{userId} {
            allow read, write: if request.auth.uid == userId;
        }
    }
}

match /projects/{projectId}/presence/{userId} {
    allow read: if request.auth != null && isCollaborator(projectId);
    allow write: if request.auth != null && request.auth.uid == userId && isCollaborator(projectId);
}

match /projects/{projectId}/scripts/{scriptId} {
    allow read, write: if request.auth != null && isCollaborator(projectId);
}

function isCollaborator(projectId) {
    return request.auth.token.email in
            get(/databases/$(database)/documents/projects/$(projectId)).data.collaborators;
}