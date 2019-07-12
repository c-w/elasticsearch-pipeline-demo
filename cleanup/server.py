import os
import subprocess

import hug


@hug.post('/')
def cleanup_project(body):
    """Cleanup cluster resources upon PR close."""
    if body.get('action') == 'closed':
        pr_info = body.get('pull_request')
        if pr_info:
            pr_id = pr_info.get('number')
            if pr_id is not None:
                with open('project_to_delete.txt', 'w', encoding='utf-8') as f:
                    project_name = f'ci-refs-pull-{pr_id}-merge'
                    f.write(project_name)
                try:
                    subprocess.check_output(['/bin/bash', '-c', './cleanup.sh'])
                    print('cleaned up:', project_name, flush=True)
                except subprocess.CalledProcessError as e:
                    print('cleanup failed:', e, flush=True)
                finally:
                    os.remove('project_to_delete.txt')
